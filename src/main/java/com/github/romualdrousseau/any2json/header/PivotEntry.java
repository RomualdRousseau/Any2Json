package com.github.romualdrousseau.any2json.header;

import com.github.romualdrousseau.any2json.base.BaseCell;

public class PivotEntry {

    public PivotEntry(final BaseCell cell) {
        this.cell = cell;
        this.pivotValue = cell.getSheet().getDocument().getModel().toEntityValue(cell.getValue()).orElse(cell.getValue());
        this.typeValue = cell.getSheet().getDocument().getModel().toEntityName(cell.getValue());
    }

    public BaseCell getCell() {
        return this.cell;
    }

    public String getValue() {
        return this.pivotValue;
    }

    public String getTypeValue() {
        return this.typeValue;
    }

    private final BaseCell cell;
    private final String pivotValue;
    private final String typeValue;
}
