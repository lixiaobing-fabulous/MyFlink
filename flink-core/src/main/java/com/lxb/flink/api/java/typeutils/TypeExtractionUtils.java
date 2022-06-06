package com.lxb.flink.api.java.typeutils;

import com.lxb.flink.api.common.functions.Function;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.flink.shaded.asm7.org.objectweb.asm.Type.getConstructorDescriptor;
import static org.apache.flink.shaded.asm7.org.objectweb.asm.Type.getMethodDescriptor;

public class TypeExtractionUtils {
    private TypeExtractionUtils() {
    }

    public static class LambdaExecutable {

        private Type[] parameterTypes;
        private Type   returnType;
        private String name;
        private Object executable;

        public LambdaExecutable(Constructor<?> constructor) {
            this.parameterTypes = constructor.getGenericParameterTypes();
            this.returnType = constructor.getDeclaringClass();
            this.name = constructor.getName();
            this.executable = constructor;
        }

        public LambdaExecutable(Method method) {
            this.parameterTypes = method.getGenericParameterTypes();
            this.returnType = method.getGenericReturnType();
            this.name = method.getName();
            this.executable = method;
        }

        public Type[] getParameterTypes() {
            return parameterTypes;
        }

        public Type getReturnType() {
            return returnType;
        }

        public String getName() {
            return name;
        }

        public boolean executablesEquals(Method m) {
            return executable.equals(m);
        }

        public boolean executablesEquals(Constructor<?> c) {
            return executable.equals(c);
        }
    }

    public static LambdaExecutable checkAndExtractLambda(Function function) throws TypeExtractionException {
        try {
            // get serialized lambda
            SerializedLambda serializedLambda = null;
            for (Class<?> clazz = function.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
                try {
                    Method replaceMethod = clazz.getDeclaredMethod("writeReplace");
                    replaceMethod.setAccessible(true);
                    Object serialVersion = replaceMethod.invoke(function);

                    // check if class is a lambda function
                    if (serialVersion != null && serialVersion.getClass() == SerializedLambda.class) {
                        serializedLambda = (SerializedLambda) serialVersion;
                        break;
                    }
                } catch (NoSuchMethodException e) {
                    // thrown if the method is not there. fall through the loop
                }
            }

            // not a lambda method -> return null
            if (serializedLambda == null) {
                return null;
            }

            // find lambda method
            String className  = serializedLambda.getImplClass();
            String methodName = serializedLambda.getImplMethodName();
            String methodSig  = serializedLambda.getImplMethodSignature();

            Class<?> implClass = Class.forName(className.replace('/', '.'), true, Thread.currentThread().getContextClassLoader());

            // find constructor
            if (methodName.equals("<init>")) {
                Constructor<?>[] constructors = implClass.getDeclaredConstructors();
                for (Constructor<?> constructor : constructors) {
                    if (getConstructorDescriptor(constructor).equals(methodSig)) {
                        return new LambdaExecutable(constructor);
                    }
                }
            }
//            // find method
            else {
                List<Method> methods = getAllDeclaredMethods(implClass);
                for (Method method : methods) {
                    if (method.getName().equals(methodName) && getMethodDescriptor(method).equals(methodSig)) {
                        return new LambdaExecutable(method);
                    }
                }
            }
            throw new TypeExtractionException("No lambda method found.");
        } catch (Exception e) {
            throw new TypeExtractionException("Could not extract lambda method out of function: " +
                    e.getClass().getSimpleName() + " - " + e.getMessage(), e);
        }
    }

    public static List<Method> getAllDeclaredMethods(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        while (clazz != null) {
            Method[] methods = clazz.getDeclaredMethods();
            Collections.addAll(result, methods);
            clazz = clazz.getSuperclass();
        }
        return result;
    }

}
