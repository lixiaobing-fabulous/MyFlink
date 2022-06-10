package com.lxb.flink.utl.function;

import java.util.function.Supplier;

import com.lxb.flink.utl.FlinkException;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-10
 */
public interface CheckedSupplier<R> extends SupplierWithException<R, Exception> {
    static <R> Supplier<R> unchecked(CheckedSupplier<R> checkedSupplier) {
        return () -> {
            try {
                return checkedSupplier.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    static <R> CheckedSupplier<R> checked(Supplier<R> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (RuntimeException e) {
                throw new FlinkException(e);
            }
        };
    }


}
