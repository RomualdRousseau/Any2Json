package com.github.romualdrousseau.any2json.header;

import java.util.Collections;
import java.util.List;

import com.github.romualdrousseau.any2json.HeaderTag;
import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.base.BaseTable;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class DataTableHeader extends BaseHeader {

    public DataTableHeader(final BaseHeader parent) {
        this(parent.getTable(), parent.getCell());
    }

    public DataTableHeader(final BaseTable table, final BaseCell cell) {
        super(table, cell);
        this.name = this.getCell().getValue();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(final String newName) {
        this.name = newName;
        this.getCell().setValue(newName);
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public List<String> entities() {
        return Collections.emptyList();
    }

    @Override
    public BaseHeader clone() {
        return new DataTableHeader(this);
    }

    @Override
    public boolean hasTag() {
        return this.tag != null;
    }

    @Override
    public HeaderTag getTag() {
        return this.tag;
    }

    public void resetTag() {
        this.tag = null;
    }

    public void updateTag() {
        if (StringUtils.isFastBlank(this.getName())) {
            this.tag = HeaderTag.None;
        } else {
            final String tagValue = this.getTable().getSheet().getDocument().getTagClassifier().predict(this.getName(), this.entities(), this.getTable().getHeaderNames());
            this.tag = new HeaderTag(tagValue);
        }
    }

    private String name;
    private HeaderTag tag;
}
