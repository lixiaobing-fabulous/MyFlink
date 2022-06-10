package com.lxb.flink.utl.function;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-10
 */
@FunctionalInterface
public interface SupplierWithException<R, E extends Throwable> {
    R get() throws E;
}
