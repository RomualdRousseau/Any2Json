package com.github.romualdrousseau.any2json.header;

import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.base.BaseTable;
import com.github.romualdrousseau.any2json.config.Settings;

public class MetaGroupHeader extends MetaTableHeader {

    public MetaGroupHeader(final BaseTable table, final BaseCell cell) {
        super(table, cell);
    }

    private MetaGroupHeader(final MetaGroupHeader parent) {
        super(parent.getTable(), parent.getCell());
    }

    @Override
    public String getName() {
        return super.getName() + " " + Settings.GROUP_VALUE_SUFFIX;
    }

    @Override
    public BaseHeader clone() {
        return new MetaGroupHeader(this);
    }
}
