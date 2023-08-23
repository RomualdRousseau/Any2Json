package com.github.romualdrousseau.any2json.intelli;

import com.github.romualdrousseau.any2json.Row;
import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.config.Settings;
import com.github.romualdrousseau.any2json.header.DataTableHeader;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class IntelliHeader extends DataTableHeader {

    public IntelliHeader(final BaseHeader header) {
        super(header.getTable(), new BaseCell(header.getName(), header.getColumnIndex(), 1,
                header.getCell().getRawValue(), header.getTable().getSheet()));

        final String cellValue = this.getCell().getValue();
        if (StringUtils.isFastBlank(cellValue)) {
            this.name = this.entities().stream().findAny().map(x -> this.getEntitiesAsString())
                    .orElse(Settings.PIVOT_VALUE_SUFFIX);
        } else {
            this.name = this.getTable().getSheet().getDocument().getModel().toEntityName(cellValue);
        }

        this.setColumnIndex(header.getColumnIndex());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public BaseCell getCellAtRow(final Row row, final boolean merged) {
        if (!merged || this.nextSibbling == null) {
            return this.getCellAtRow(row);
        }
        String buffer = "";
        IntelliHeader curr = this;
        while (curr != null) {
            final String value = curr.getCellAtRow(row).getValue();
            if (!buffer.contains(value)) {
                buffer += value;
            }
            curr = curr.nextSibbling;
        }
        if (buffer.isEmpty()) {
            return this.getCellAtRow(row);
        } else {
            return new BaseCell(buffer, this.getColumnIndex(), 1, this.getTable().getSheet());
        }
    }

    @Override
    public BaseHeader clone() {
        return new IntelliHeader(this);
    }

    @Override
    public void resetTag() {
        super.resetTag();
        this.nextSibbling = null;
    }

    public void mergeTo(final IntelliHeader other) {
        IntelliHeader e = this;
        while (e.nextSibbling != null) {
            e = e.nextSibbling;
        }
        e.nextSibbling = other;
    }

    private final String name;
    private IntelliHeader nextSibbling;
}
