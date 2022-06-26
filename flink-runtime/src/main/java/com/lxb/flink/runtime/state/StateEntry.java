package com.lxb.flink.runtime.state;

public interface StateEntry<K, N, S> {
    K getKey();

    N getNamespace();

    S getState();

    class SimpleStateEntry<K, N, S> implements StateEntry<K, N, S> {
        private final K key;
        private final N namespace;
        private final S value;

        public SimpleStateEntry(K key, N namespace, S value) {
            this.key = key;
            this.namespace = namespace;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public N getNamespace() {
            return namespace;
        }

        @Override
        public S getState() {
            return value;
        }
    }

}
