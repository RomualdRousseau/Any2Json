package com.github.romualdrousseau.any2json.header;

import com.github.romualdrousseau.any2json.HeaderTag;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.base.BaseTable;
import com.github.romualdrousseau.any2json.base.BaseCell;

public abstract class CompositeHeader extends BaseHeader {

    public abstract CompositeHeader clone();

    public CompositeHeader(final BaseTable table, final BaseCell cell) {
        super(table, cell);
    }

    @Override
    public boolean hasTag() {
        return false;
    }

    @Override
    public HeaderTag getTag() {
        return null;
    }

    public BaseTable getTable() {
        return (BaseTable) super.getTable();
    }
}
