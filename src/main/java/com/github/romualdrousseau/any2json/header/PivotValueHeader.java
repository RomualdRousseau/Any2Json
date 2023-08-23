package com.github.romualdrousseau.any2json.header;

public class PivotValueHeader extends PivotKeyHeader {

    public PivotValueHeader(final PivotKeyHeader parent) {
        super(parent.getTable(), parent.getCell());
        this.name = parent.getValueName();
    }

    @Override
    public String getName() {
        if(!this.getTable().isLoadCompleted()) {
            return this.getTable().getSheet().getPivotValueFormat();
        } else {
            return String.format(this.getTable().getSheet().getPivotValueFormat(), this.name);
        }
    }

    @Override
    public PivotValueHeader clone() {
        return new PivotValueHeader(this);
    }

    private String name;
}
