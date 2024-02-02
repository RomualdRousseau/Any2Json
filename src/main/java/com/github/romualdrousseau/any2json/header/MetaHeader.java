package com.github.romualdrousseau.any2json.header;

import java.util.Collections;
import java.util.List;

import com.github.romualdrousseau.any2json.HeaderTag;
import com.github.romualdrousseau.any2json.Row;
import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.base.BaseTable;

public class MetaHeader extends BaseHeader {

    public MetaHeader(final BaseTable table, final BaseCell cell) {
        super(table, cell);

        final var model = this.getTable().getSheet().getDocument().getModel();
        final var cellValue = this.getCell().getValue();
        this.transformedCell = model
                .toEntityValue(cellValue)
                .map(x -> new BaseCell(x, this.getCell()))
                .orElse(this.getCell());
        this.name = this.getPivotEntityAsString().orElseGet(() -> model.toEntityName(cellValue));
        this.value = this.transformedCell.getValue();
    }

    private MetaHeader(final MetaHeader parent) {
        this(parent.getTable(), parent.getCell());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public BaseCell getCellAtRow(final Row row) {
        return this.transformedCell;
    }

    @Override
    public BaseHeader clone() {
        return new MetaHeader(this);
    }

    @Override
    public List<String> entities() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasTag() {
        return false;
    }

    @Override
    public HeaderTag getTag() {
        return null;
    }

    private final String name;
    private final String value;
    private final BaseCell transformedCell;
}
