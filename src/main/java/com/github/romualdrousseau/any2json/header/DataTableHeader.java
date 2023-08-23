package com.github.romualdrousseau.any2json.header;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.github.romualdrousseau.any2json.HeaderTag;
import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.base.BaseRow;
import com.github.romualdrousseau.any2json.base.BaseTable;
import com.github.romualdrousseau.any2json.config.Settings;
import com.github.romualdrousseau.shuju.strings.StringUtils;
import com.github.romualdrousseau.shuju.types.Tensor;

public class DataTableHeader extends BaseHeader {

    public DataTableHeader(final BaseHeader parent) {
        this(parent.getTable(), parent.getCell());
    }

    public DataTableHeader(final BaseTable table, final BaseCell cell) {
        super(table, cell);
        this.name = this.getCell().getValue();
        this.entities = null;
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
        if (this.entities == null) {
            if (this.getColumnIndex() < 0 || this.getColumnIndex() >= this.getTable().getNumberOfColumns()) {
                this.entities =  Collections.emptyList();
            } else {
                this.entities = this.sampleEntities();
            }
        }
        return this.entities;
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

    private List<String> sampleEntities() {
        final int N = Math.min(this.getTable().getNumberOfRows(), Settings.DEFAULT_SAMPLE_COUNT);
        final Tensor entityVector = Tensor.zeros(this.getTable().getSheet().getDocument().getModel().getEntityList().size());
        float n = 0.0f;
        for (int i = 0; i < N; i++) {
            final BaseRow row = this.getTable().getRowAt(i);
            if (row == null) {
                continue;
            }
            final BaseCell cell = row.getCellAt(this.getColumnIndex());
            if (cell.hasValue() && cell.getSymbol().equals("e")) {
                entityVector.iadd(cell.getEntityVector());
                n += Settings.DEFAULT_ENTITY_PROBABILITY;
            }
        }
        if (n > 0.0f) {
            entityVector.if_lt_then(n, 0.0f, 1.0f);
        }
        final List<String> entityList = this.getTable().getSheet().getDocument().getModel().getEntityList();
        return IntStream.range(0, entityVector.size).boxed().filter(i -> entityVector.data[i] == 1).map(i -> entityList.get(i)).toList();
    }

    private String name;
    private HeaderTag tag;
    private List<String> entities;
}
