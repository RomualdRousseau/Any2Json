package com.github.romualdrousseau.any2json.intelli.header;

import com.github.romualdrousseau.any2json.intelli.CompositeTable;

import java.util.List;

import com.github.romualdrousseau.any2json.base.BaseCell;

public class DataTableHeader extends CompositeHeader {

    public DataTableHeader(final CompositeTable table, final BaseCell cell) {
        super(table, cell);  
        this.name = this.getCell().getValue();
    }

    private DataTableHeader(final DataTableHeader parent) {
        this(parent.getTable(), parent.getCell());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public List<String> entities() {
        return null;
    }

    @Override
    public CompositeHeader clone() {
        return new DataTableHeader(this);
    }

    public void updateName(final String newName) {
        this.name = newName;
        this.getCell().setValue(newName);
    }

    private String name;
}
