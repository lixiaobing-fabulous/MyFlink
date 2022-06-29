package com.lxb.flink.api.common.io;

import java.io.IOException;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-29
 */
public interface FinalizeOnMaster {
    void finalizeGlobal(int parallelism) throws IOException;

}
