package com.github.romualdrousseau.any2json.v2.loader.excel.xml;

import com.github.romualdrousseau.shuju.util.StringUtility;

import nl.fountain.xelem.excel.Cell;
import nl.fountain.xelem.excel.Row;
import nl.fountain.xelem.excel.Worksheet;

import com.github.romualdrousseau.any2json.v2.DocumentFactory;
import com.github.romualdrousseau.any2json.v2.intelli.IntelliSheet;
import com.github.romualdrousseau.any2json.v2.util.RowTranslatable;
import com.github.romualdrousseau.any2json.v2.util.RowTranslator;

class XmlSheet extends IntelliSheet implements RowTranslatable {

    public XmlSheet(Worksheet sheet) {
        this.sheet = sheet;
        this.rowTranslator = new RowTranslator(this);
    }

    @Override
    public String getName() {
        return this.sheet.getName();
    }

    @Override
    public int getLastColumnNum(int rowIndex) {
        Row row = this.getRowAt(rowIndex);
        if (row == null) {
            return 0;
        }

        return row.maxCellIndex();
    }

    @Override
    public int getLastRowNum() {
        return this.sheet.getRows().size() - this.rowTranslator.getIgnoredRowCount() - 1;
    }

    @Override
    public boolean hasCellDataAt(int colIndex, int rowIndex) {
        Cell cell = this.getCellAt(colIndex, rowIndex);
        return cell != null;
    }

    @Override
    public String getInternalCellValueAt(int colIndex, int rowIndex) {
        Cell cell = this.getCellAt(colIndex, rowIndex);
        if (cell == null) {
            return null;
        }
        return StringUtility.cleanToken(cell.getData$());
    }

    @Override
    public int getNumberOfMergedCellsAt(int colIndex, int rowIndex) {
        Cell cell = this.getCellAt(colIndex, rowIndex);
        if (cell == null) {
            return 1;
        }
        return cell.getMergeAcross() + 1;
    }

    @Override
    public boolean isIgnorableRow(int rowIndex) {
        if (rowIndex >= this.sheet.getRows().size()) {
            return false;
        }

        Row row = this.sheet.getRowAt(rowIndex + 1);
        if (row == null) {
            return false;
        }

        double height = this.sheet.getRowAt(rowIndex + 1).getHeight();

        int countEmptyCells = 0;
        int countCells = 0;
        boolean checkIfRowMergedVertically = false;
        for (Cell cell : row.getCells()) {
            if (!cell.hasData() || cell.getData$().isEmpty()) {
                countEmptyCells++;
            }
            if (!checkIfRowMergedVertically && this.getMergeDown(cell, rowIndex) > 0) {
                checkIfRowMergedVertically = true;
            }
            countCells++;
        }

        float sparcity = (countCells == 0) ? 1.0f : (Float.valueOf(countEmptyCells) / Float.valueOf(countCells));

        boolean candidate = false;
        candidate |= (height < DocumentFactory.SEPARATOR_ROW_THRESHOLD);
        candidate |= checkIfRowMergedVertically;
        candidate &= (sparcity >= DocumentFactory.DEFAULT_RATIO_SCARSITY);
        return candidate;
    }

    private Row getRowAt(int rowIndex) {
        final int translatedRow = this.rowTranslator.rebase(rowIndex);
        if (translatedRow == -1) {
            return null;
        }
        Row row = this.sheet.getRowAt(translatedRow + 1);
        if (row == null) {
            return null;
        }
        return row;
    }

    private Cell getCellAt(int colIndex, int rowIndex) {
        final int translatedRow = this.rowTranslator.rebase(rowIndex);
        if (translatedRow == -1) {
            return null;
        }
        Cell cell = this.sheet.getCellAt(translatedRow + 1, colIndex + 1);
        if (!cell.hasData()) {
            return null;
        }
        return cell;
    }

    private int getMergeDown(Cell cell, int rowIndex) {
        if (rowIndex <= 0) {
            return 0;
        }
        if (cell == null) {
            return 0;
        }

        int numberOfCells = 0;
        for (int i = 1; i < 5; i++) {
            int firstRow = rowIndex - i;
            if (firstRow < 0) {
                break;
            }

            int lastRow = firstRow + this.sheet.getCellAt(firstRow + 1, cell.getIndex() + 1).getMergeDown();

            if (lastRow > firstRow && firstRow <= rowIndex && rowIndex <= lastRow) {
                numberOfCells = firstRow - lastRow;
            }
        }

        return numberOfCells;
    }

    private Worksheet sheet;
    private RowTranslator rowTranslator;
}
