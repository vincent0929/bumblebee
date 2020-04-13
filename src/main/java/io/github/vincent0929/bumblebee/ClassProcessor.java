package io.github.vincent0929.bumblebee;

public interface ClassProcessor {

    boolean isSupport(Class<?> clazz);

    void process(Class<?> clazz);
}
