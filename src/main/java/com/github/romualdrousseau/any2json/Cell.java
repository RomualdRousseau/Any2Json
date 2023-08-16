package com.github.romualdrousseau.any2json;

public interface Cell {

    boolean hasValue();

    String getValue();

    Iterable<String> entities();

    String getEntitiesAsString();
}
