package com.lxb.flink.utl;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-10
 */
public class FlinkException extends Exception {

    public FlinkException() {
    }

    public FlinkException(String message) {
        super(message);
    }

    public FlinkException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlinkException(Throwable cause) {
        super(cause);
    }
}
