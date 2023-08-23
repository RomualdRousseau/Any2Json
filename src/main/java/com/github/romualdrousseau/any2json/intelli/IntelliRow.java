package com.github.romualdrousseau.any2json.intelli;

import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseRow;

public class IntelliRow extends BaseRow {

    public IntelliRow(final IntelliTable table, int capacity) {
        super(table, -1);
        this.cellsData = new BaseCell[capacity];
    }

    @Override
    public BaseCell getCellAt(final int colIndex) {
        assert (colIndex >= 0 && colIndex < this.getTable().getNumberOfColumns()) : "column index out of bound";
        final BaseCell cell = this.cellsData[colIndex];
        return (cell == null) ? BaseCell.Empty : cell;
    }

    public void setCell(final int colIndex, final String value, final String rawValue) {
        if(value != null) {
            this.setCell(colIndex, new BaseCell(value, colIndex, 1, rawValue, this.getTable().getSheet()));
        }
    }

    public void setCell(final int colIndex, final BaseCell cell) {
        assert (colIndex < this.cellsData.length) : "column index out of bound";
        if (cell.hasValue()) {
            if (cell.getMergedCount() == 1) {
                this.cellsData[colIndex] = cell;
            } else {
                this.cellsData[colIndex] = new BaseCell(cell.getValue(), colIndex, 1, cell.getRawValue(), this.getTable().getSheet());
            }
        }
    }

    private final BaseCell[] cellsData;
}
