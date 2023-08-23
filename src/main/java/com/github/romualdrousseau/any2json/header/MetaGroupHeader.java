package com.github.romualdrousseau.any2json.header;

import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.base.BaseTable;

public class MetaGroupHeader extends MetaTableHeader {

    public MetaGroupHeader(final BaseTable table, final BaseCell cell) {
        super(table, cell);
    }

    private MetaGroupHeader(final MetaGroupHeader parent) {
        super(parent.getTable(), parent.getCell());
    }

    @Override
    public String getName() {
        return String.format(this.getTable().getSheet().getGroupValueFormat(), super.getName());
    }

    @Override
    public BaseHeader clone() {
        return new MetaGroupHeader(this);
    }
}
