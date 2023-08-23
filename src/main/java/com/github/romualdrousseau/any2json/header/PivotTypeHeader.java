package com.github.romualdrousseau.any2json.header;

public class PivotTypeHeader extends PivotKeyHeader {

    public PivotTypeHeader(final PivotKeyHeader parent, final String name) {
        super(parent.getTable(), parent.getCell());
        this.name = name;
    }

    @Override
    public String getName() {
        if(!this.getTable().isLoadCompleted()) {
            return this.getTable().getSheet().getPivotTypeFormat();
        } else {
            return String.format(this.getTable().getSheet().getPivotTypeFormat(), this.name);
        }
    }

    @Override
    public PivotValueHeader clone() {
        return new PivotValueHeader(this, this.name);
    }

    private String name;
}
