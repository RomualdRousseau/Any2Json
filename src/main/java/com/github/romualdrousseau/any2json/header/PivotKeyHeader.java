package com.github.romualdrousseau.any2json.header;

import java.util.ArrayList;
import java.util.List;

import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.base.BaseTable;

public class PivotKeyHeader extends MetaHeader {

    public PivotKeyHeader(final BaseTable table, final BaseCell cell) {
        super(table, cell);
        this.entries = new ArrayList<>();
        this.entries.add(new PivotEntry(cell));
        this.valueName = this.getPivotEntityAsString().get();
    }

    private PivotKeyHeader(final PivotKeyHeader parent) {
        super(parent.getTable(), parent.getCell());
        this.entries = parent.entries;
        this.valueName = this.getPivotEntityAsString().get();
    }

    @Override
    public String getName() {
        return String.format(this.getTable().getSheet().getPivotKeyFormat(), super.getName());
    }

    @Override
    public BaseHeader clone() {
        return new PivotKeyHeader(this);
    }

    public List<PivotEntry> getEntries() {
        return this.entries;
    }

    public void addEntry(final BaseCell entry) {
        this.entries.add(new PivotEntry(entry));
    }

    public String getValueName() {
        return this.valueName;
    }

    public void updateValueName(final String newName) {
        this.valueName = newName;
    }

    public PivotValueHeader getPivotValue() {
        return new PivotValueHeader(this, this.valueName);
    }

    public PivotTypeHeader getPivotType() {
        return new PivotTypeHeader(this, this.valueName);
    }

    private final List<PivotEntry> entries;
    private String valueName;
}
