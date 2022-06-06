package com.lxb.flink.api.java.typeutils;

import com.lxb.flink.api.common.functions.FlatMapFunction;
import com.lxb.flink.api.common.functions.Function;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.utl.Preconditions;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public class TypeExtractor {
    public static TypeInformation<Object> getForClass(Class<Object> objectClass) {
        return null;
    }

    public static <IN, OUT> TypeInformation<OUT> getFlatMapReturnTypes(FlatMapFunction<IN, OUT> flatMapInterface, TypeInformation<IN> inType,
                                                                       String functionName, boolean allowMissing) {
        return getUnaryOperatorReturnType(
                (Function) flatMapInterface,
                FlatMapFunction.class,
                0,
                1,
                new int[]{1, 0},
                inType,
                functionName,
                allowMissing);
    }

    public static <IN, OUT> TypeInformation<OUT> getUnaryOperatorReturnType(
            Function function,
            Class<?> baseClass,
            int inputTypeArgumentIndex,
            int outputTypeArgumentIndex,
            int[] lambdaOutputTypeArgumentIndices,
            TypeInformation<IN> inType,
            String functionName,
            boolean allowMissing) {
        Preconditions.checkArgument(inType == null || inputTypeArgumentIndex >= 0, "Input type argument index was not provided");
        Preconditions.checkArgument(outputTypeArgumentIndex >= 0, "Output type argument index was not provided");
        Preconditions.checkArgument(
                lambdaOutputTypeArgumentIndices != null,
                "Indices for output type arguments within lambda not provided");

        if (function instanceof ResultTypeQueryable) {
            return ((ResultTypeQueryable<OUT>) function).getProducedType();
        }
//
//        // perform extraction
//        try {
//            final LambdaExecutable exec;
//            try {
//                exec = checkAndExtractLambda(function);
//            } catch (TypeExtractionException e) {
//                throw new InvalidTypesException("Internal error occurred.", e);
//            }
//            if (exec != null) {
//
//                // parameters must be accessed from behind, since JVM can add additional parameters e.g. when using local variables inside lambda function
//                // paramLen is the total number of parameters of the provided lambda, it includes parameters added through closure
//                final int paramLen = exec.getParameterTypes().length;
//
//                final Method sam = TypeExtractionUtils.getSingleAbstractMethod(baseClass);
//
//                // number of parameters the SAM of implemented interface has; the parameter indexing applies to this range
//                final int baseParametersLen = sam.getParameterTypes().length;
//
//                final Type output;
//                if (lambdaOutputTypeArgumentIndices.length > 0) {
//                    output = TypeExtractionUtils.extractTypeFromLambda(
//                            baseClass,
//                            exec,
//                            lambdaOutputTypeArgumentIndices,
//                            paramLen,
//                            baseParametersLen);
//                } else {
//                    output = exec.getReturnType();
//                    TypeExtractionUtils.validateLambdaType(baseClass, output);
//                }
//
//                return new TypeExtractor().privateCreateTypeInfo(output, inType, null);
//            } else {
//                if (inType != null) {
//                    validateInputType(baseClass, function.getClass(), inputTypeArgumentIndex, inType);
//                }
//                return new TypeExtractor().privateCreateTypeInfo(baseClass, function.getClass(), outputTypeArgumentIndex, inType, null);
//            }
//        }
//        catch (InvalidTypesException e) {
//            if (allowMissing) {
//                return (TypeInformation<OUT>) new MissingTypeInfo(functionName != null ? functionName : function.toString(), e);
//            } else {
//                throw e;
//            }
//        }
        return null;
    }


}
