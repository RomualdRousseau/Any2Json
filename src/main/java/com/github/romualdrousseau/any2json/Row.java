package com.github.romualdrousseau.any2json;

public interface Row {

    boolean isEmpty();

    int getRowNum();

    int getNumberOfCells();

    Cell getCellAt(final int colIndex);

    Iterable<Cell> cells();
}
