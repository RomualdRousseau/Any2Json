package com.github.romualdrousseau.any2json;

public interface Header {

    String getName();

    Cell getCellAtRow(final Row row);

    Cell getCellAtRow(final Row row, final boolean merged);

    boolean hasTag();

    HeaderTag getTag();

    Iterable<String> entities();

    String getEntitiesAsString();

    boolean isColumnEmpty();

    boolean isColumnMerged();
}
