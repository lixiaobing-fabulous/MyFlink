package com.lxb.flink.runtime.operators.util;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-29
 */
public class CorruptConfigurationException extends RuntimeException
{
    private static final long serialVersionUID = 854450995262666207L;

    /**
     * Creates a new exception with the given error message.
     *
     * @param message The exception's message.
     */
    public CorruptConfigurationException(String message) {
        super(message);
    }

    public CorruptConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
