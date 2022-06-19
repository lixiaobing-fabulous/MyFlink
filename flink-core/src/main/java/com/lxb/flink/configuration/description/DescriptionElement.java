package com.lxb.flink.configuration.description;

interface DescriptionElement {
    /**
     * Transforms itself into String representation using given format.
     *
     * @param formatter formatter to use.
     */
    void format(Formatter formatter);
}
