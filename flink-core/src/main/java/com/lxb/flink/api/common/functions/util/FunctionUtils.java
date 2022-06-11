package com.lxb.flink.api.common.functions.util;

import com.lxb.flink.api.common.functions.Function;
import com.lxb.flink.api.common.functions.RichFunction;
import com.lxb.flink.api.common.functions.RuntimeContext;
import com.lxb.flink.configuration.Configuration;

public final class FunctionUtils {
    public static void openFunction(Function function, Configuration parameters) throws Exception {
        if (function instanceof RichFunction) {
            RichFunction richFunction = (RichFunction) function;
            richFunction.open(parameters);
        }
    }

    public static void closeFunction(Function function) throws Exception {
        if (function instanceof RichFunction) {
            RichFunction richFunction = (RichFunction) function;
            richFunction.close();
        }
    }

    public static void setFunctionRuntimeContext(Function function, RuntimeContext context) {
        if (function instanceof RichFunction) {
            RichFunction richFunction = (RichFunction) function;
            richFunction.setRuntimeContext(context);
        }
    }

    public static RuntimeContext getFunctionRuntimeContext(Function function, RuntimeContext defaultContext) {
        if (function instanceof RichFunction) {
            RichFunction richFunction = (RichFunction) function;
            return richFunction.getRuntimeContext();
        } else {
            return defaultContext;
        }
    }

    private FunctionUtils() {
        throw new RuntimeException();
    }
}
