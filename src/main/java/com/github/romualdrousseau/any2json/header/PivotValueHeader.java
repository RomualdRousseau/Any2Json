package com.github.romualdrousseau.any2json.header;

import com.github.romualdrousseau.any2json.DocumentFactory;

public class PivotValueHeader extends PivotKeyHeader {

    public PivotValueHeader(final PivotKeyHeader parent) {
        super(parent.getTable(), parent.getCell());
        this.name = parent.getValueName();
    }

    @Override
    public String getName() {
        if(!this.getTable().isLoadCompleted()) {
            return DocumentFactory.PIVOT_VALUE_SUFFIX;
        } else {
            return this.name + " " + DocumentFactory.PIVOT_VALUE_SUFFIX;
        }
    }

    @Override
    public PivotValueHeader clone() {
        return new PivotValueHeader(this);
    }

    private String name;
}
