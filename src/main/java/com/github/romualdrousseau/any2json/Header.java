package com.github.romualdrousseau.any2json;

public interface Header {

    String getName();

    Cell getCellAtRow(Row row);

    Cell getCellAtRow(Row row, boolean merged);

    boolean hasTag();

    HeaderTag getTag();

    Iterable<String> entities();

    String getEntitiesAsString();
}
