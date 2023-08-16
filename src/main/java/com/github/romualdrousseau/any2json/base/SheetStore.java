package com.github.romualdrousseau.any2json.base;

public interface SheetStore {

    int getLastColumnNum(int rowIndex);

    int getLastRowNum();

    boolean hasCellDataAt(int colIndex, int rowIndex);

    boolean hasCellDecorationAt(int colIndex, int rowIndex);

    String getCellDataAt(int colIndex, int rowIndex);

    int getNumberOfMergedCellsAt(int colIndex, int rowIndex);

    void patchCell(int colIndex1, int rowIndex1, int colIndex2, int rowIndex2, String value, boolean unmergeAll);
}
