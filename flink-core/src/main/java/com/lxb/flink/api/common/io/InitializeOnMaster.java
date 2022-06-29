package com.lxb.flink.api.common.io;

import java.io.IOException;


import com.lxb.flink.annotation.Public;

@Public
public interface InitializeOnMaster {
	void initializeGlobal(int parallelism) throws IOException;
}
