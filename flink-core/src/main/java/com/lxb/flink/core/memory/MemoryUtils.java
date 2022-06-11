package com.lxb.flink.core.memory;

import com.lxb.flink.utl.Preconditions;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MemoryUtils {
    public static final  sun.misc.Unsafe UNSAFE                       = getUnsafe();
    public static final  ByteOrder       NATIVE_BYTE_ORDER            = ByteOrder.nativeOrder();
    private static final long            BUFFER_ADDRESS_FIELD_OFFSET  = getClassFieldOffset(Buffer.class, "address");
    private static final long            BUFFER_CAPACITY_FIELD_OFFSET = getClassFieldOffset(Buffer.class, "capacity");
    private static final Class<?>        DIRECT_BYTE_BUFFER_CLASS     = getClassByName("java.nio.DirectByteBuffer");

    private static sun.misc.Unsafe getUnsafe() {
        try {
            Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            return (sun.misc.Unsafe) unsafeField.get(null);
        } catch (SecurityException e) {
            throw new Error("Could not access the sun.misc.Unsafe handle, permission denied by security manager.", e);
        } catch (NoSuchFieldException e) {
            throw new Error("The static handle field in sun.misc.Unsafe was not found.", e);
        } catch (IllegalArgumentException e) {
            throw new Error("Bug: Illegal argument reflection access for static field.", e);
        } catch (IllegalAccessException e) {
            throw new Error("Access to sun.misc.Unsafe is forbidden by the runtime.", e);
        } catch (Throwable t) {
            throw new Error("Unclassified error while trying to access the sun.misc.Unsafe handle.", t);
        }
    }

    private static long getClassFieldOffset(@SuppressWarnings("SameParameterValue") Class<?> cl, String fieldName) {
        try {
            return UNSAFE.objectFieldOffset(cl.getDeclaredField(fieldName));
        } catch (SecurityException e) {
            throw new Error(getClassFieldOffsetErrorMessage(cl, fieldName) + ", permission denied by security manager.", e);
        } catch (NoSuchFieldException e) {
            throw new Error(getClassFieldOffsetErrorMessage(cl, fieldName), e);
        } catch (Throwable t) {
            throw new Error(getClassFieldOffsetErrorMessage(cl, fieldName) + ", unclassified error", t);
        }
    }

    private static String getClassFieldOffsetErrorMessage(Class<?> cl, String fieldName) {
        return "Could not get field '" + fieldName + "' offset in class '" + cl + "' for unsafe operations";
    }

    private static Class<?> getClassByName(@SuppressWarnings("SameParameterValue") String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new Error("Could not find class '" + className + "' for unsafe operations.", e);
        }
    }

    static long allocateUnsafe(long size) {
        return UNSAFE.allocateMemory(Math.max(1L, size));
    }

    //    static Runnable createMemoryGcCleaner(Object owner, long address, Runnable customCleanup) {
//        return JavaGcCleanerWrapper.createCleaner(owner, () -> {
//            releaseUnsafe(address);
//            customCleanup.run();
//        });
//    }
    private static void releaseUnsafe(long address) {
        UNSAFE.freeMemory(address);
    }

    static ByteBuffer wrapUnsafeMemoryWithByteBuffer(long address, int size) {
        //noinspection OverlyBroadCatchBlock
        try {
            ByteBuffer buffer = (ByteBuffer) UNSAFE.allocateInstance(DIRECT_BYTE_BUFFER_CLASS);
            UNSAFE.putLong(buffer, BUFFER_ADDRESS_FIELD_OFFSET, address);
            UNSAFE.putInt(buffer, BUFFER_CAPACITY_FIELD_OFFSET, size);
            buffer.clear();
            return buffer;
        } catch (Throwable t) {
            throw new Error("Failed to wrap unsafe off-heap memory with ByteBuffer", t);
        }
    }

    static long getByteBufferAddress(ByteBuffer buffer) {
        Preconditions.checkNotNull(buffer, "buffer is null");
        Preconditions.checkArgument(buffer.isDirect(), "Can't get address of a non-direct ByteBuffer.");
        try {
            return UNSAFE.getLong(buffer, BUFFER_ADDRESS_FIELD_OFFSET);
        } catch (Throwable t) {
            throw new Error("Could not access direct byte buffer address field.", t);
        }
    }

    private MemoryUtils() {
    }
}
