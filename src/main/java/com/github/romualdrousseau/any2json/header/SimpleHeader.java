package com.github.romualdrousseau.any2json.header;

import com.github.romualdrousseau.any2json.HeaderTag;
import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.base.BaseTable;

public class SimpleHeader extends BaseHeader {

    public SimpleHeader(final BaseTable table, final BaseCell cell) {
        super(table, cell);
    }

    public SimpleHeader(final BaseHeader parent) {
        super(parent.getTable(), parent.getCell());
    }

    @Override
    public String getName() {
        return this.getCell().getValue();
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public boolean hasTag() {
        return false;
    }

    @Override
    public HeaderTag getTag() {
        return null;
    }
}
