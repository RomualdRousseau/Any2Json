package com.github.romualdrousseau.any2json.header;

import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.base.BaseTable;
import com.github.romualdrousseau.any2json.base.RowGroup;

public class MetaTableHeader extends MetaHeader {

    public MetaTableHeader(final BaseTable table, final BaseCell cell) {
        super(table, cell);
        this.rowGroup = null;
    }

    private MetaTableHeader(final MetaTableHeader parent) {
        this(parent.getTable(), parent.getCell());
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public BaseHeader clone() {
        return new MetaTableHeader(this);
    }

    @Override
    public boolean hasRowGroup() {
        return this.rowGroup != null;
    }

	public void assignRowGroup(final RowGroup rowGroup) {
        this.rowGroup = rowGroup;
    }

    private RowGroup rowGroup;
}
