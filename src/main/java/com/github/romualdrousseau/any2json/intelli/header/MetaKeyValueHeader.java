package com.github.romualdrousseau.any2json.intelli.header;

import com.github.romualdrousseau.any2json.Row;
import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.intelli.CompositeTable;

public class MetaKeyValueHeader extends MetaHeader {

    public MetaKeyValueHeader(final CompositeTable table, final BaseCell key, final BaseCell value) {
        super(table, key);
        this.name = this.getCell().getValue();
        this.value = value;
        this.valueOfValue = this.getLayoutClassifier().toEntityValue(value.getValue()).orElse(value.getValue());
    }

    private MetaKeyValueHeader(final MetaKeyValueHeader parent) {
        this(parent.getTable(), parent.getCell(), parent.value);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.valueOfValue;
    }

    @Override
    public BaseCell getCellAtRow(final Row row) {
        return this.value;
    }

    @Override
    public String getEntitiesAsString() {
        return this.value.getEntitiesAsString();
    }

    @Override
    public CompositeHeader clone() {
        return new MetaKeyValueHeader(this);
    }

    private final String name;
    private final BaseCell value;
    private final String valueOfValue;
}
