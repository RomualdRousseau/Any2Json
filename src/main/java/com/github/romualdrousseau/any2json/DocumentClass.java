package com.github.romualdrousseau.any2json;

public interface DocumentClass {

    enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    Priority getPriority();

    Document newInstance();
}
