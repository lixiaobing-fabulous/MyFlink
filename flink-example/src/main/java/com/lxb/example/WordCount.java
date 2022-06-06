package com.lxb.example;

import com.lxb.flink.api.common.functions.FlatMapFunction;
import com.lxb.flink.api.common.functions.ReduceFunction;
import com.lxb.flink.api.environment.StreamExecutionEnvironment;
import com.lxb.flink.api.java.tupple.Tuple2;
import com.lxb.flink.utl.Collector;

public class WordCount {
    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        env.fromElements("hello world", "hello world")
                .flatMap(new Tokenizer())
                .setParallelism(1)
                .filter(r -> r.f0.equals("hello"))
                .setParallelism(2)
                .keyBy(r -> r.f0)
                .reduce(
                        new ReduceFunction<Tuple2<String, Integer>>() {
                            @Override
                            public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
                                return Tuple2.of(value1.f0, value1.f1 + value2.f1);
                            }
                        }
                )
                .setParallelism(2)
                .print();

        env.execute("Streaming WordCount");
    }

    public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            String[] tokens = value.toLowerCase().split("\\W+");

            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }
}
