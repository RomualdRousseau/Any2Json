package com.github.romualdrousseau.any2json.base;

import java.util.Optional;

import com.github.romualdrousseau.any2json.Header;
import com.github.romualdrousseau.any2json.Row;

public abstract class BaseHeader implements Header {

    public BaseHeader(final BaseTable table, final BaseCell cell) {
        this.table = table;
        this.cell = cell;
        this.colIndex = cell.getColumnIndex();
        this.columnEmpty = false;
    }

    @Override
    public BaseCell getCellAtRow(final Row row) {
        return ((BaseRow) row).getCellAt(this.getColumnIndex());
    }

    @Override
    public BaseCell getCellAtRow(final Row row, final boolean merged) {
        return this.getCellAtRow(row);
    }

    @Override
    public String getEntitiesAsString() {
        return String.join("|", this.entities());
    }

    @Override
    public Iterable<String> entities() {
        return this.getCell().entities();
    }

    @Override
    public boolean isColumnEmpty() {
        return this.columnEmpty;
    }

    public void setColumnEmpty(boolean columnEmpty) {
        this.columnEmpty = columnEmpty;
    }

    public int getColumnIndex() {
        return this.colIndex;
    }

    public void setColumnIndex(final int colIndex) {
        this.colIndex = colIndex;
    }

    public BaseTable getTable() {
        return this.table;
    }

    public void setTable(final BaseTable table) {
        this.table = table;
    }

    public BaseCell getCell() {
        return this.cell;
    }

    public boolean hasRowGroup() {
        return false;
    }

    public boolean isPivotHeader() {
        return this.cell.isPivotHeader();
    }

    public Optional<String> getPivotEntityAsString() {
        return this.cell.getPivotEntityAsString();
    }

    public boolean equals(final BaseHeader o) {
        return this.getName().equalsIgnoreCase(o.getName());
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof BaseHeader && this.equals((BaseHeader) o);
    }

    public abstract String getValue();

    public abstract BaseHeader clone();

    private BaseTable table;
    private final BaseCell cell;
    private int colIndex;
    private boolean columnEmpty;
}
