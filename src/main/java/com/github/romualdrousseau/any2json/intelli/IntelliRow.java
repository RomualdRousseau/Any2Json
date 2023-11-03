package com.github.romualdrousseau.any2json.intelli;

import com.github.romualdrousseau.any2json.base.BaseRow;
import com.github.romualdrousseau.any2json.base.BaseTable;
import com.github.romualdrousseau.shuju.bigdata.Row;

public class IntelliRow extends BaseRow {

    public IntelliRow(final BaseTable table, final int rowIndex, final Row row) {
        super(table, rowIndex);
        this.row = row;
    }

    @Override
    protected String getCellValueAt(final int colIndex) {
        return this.row.get(colIndex);
    }

    @Override
    protected int getNumberOfMergedCellsAt(final int colIndex) {
        return 1;
    }

    private final Row row;
}
