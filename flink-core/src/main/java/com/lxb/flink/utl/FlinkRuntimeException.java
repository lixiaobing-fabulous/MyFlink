package com.lxb.flink.utl;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-10
 */
public class FlinkRuntimeException extends RuntimeException{
    public FlinkRuntimeException() {
    }

    public FlinkRuntimeException(String message) {
        super(message);
    }

    public FlinkRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlinkRuntimeException(Throwable cause) {
        super(cause);
    }
}
