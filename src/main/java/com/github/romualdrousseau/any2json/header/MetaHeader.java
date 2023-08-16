package com.github.romualdrousseau.any2json.header;

import java.util.List;

import com.github.romualdrousseau.any2json.Row;
import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseTable;

public class MetaHeader extends CompositeHeader {

    public MetaHeader(final BaseTable table, final BaseCell cell) {
        super(table, cell);

        final String cellValue = this.getCell().getValue();
        this.transformedCell = this.getTable().getSheet().getDocument().getModel()
                .toEntityValue(cellValue)
                .map(x -> new BaseCell(x, this.getCell()))
                .orElse(this.getCell());
        this.name = this.getPivotEntityAsString().orElseGet(() -> this.getTable().getSheet().getDocument().getModel().toEntityName(cellValue));
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
    public CompositeHeader clone() {
        return new MetaHeader(this);
    }

    @Override
    public List<String> entities() {
        return null;
    }

    private final String name;
    private final String value;
    private final BaseCell transformedCell;
}
