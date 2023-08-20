package com.github.romualdrousseau.any2json.header;

import java.util.List;
import java.util.stream.IntStream;

import com.github.romualdrousseau.any2json.DocumentFactory;
import com.github.romualdrousseau.any2json.HeaderTag;
import com.github.romualdrousseau.any2json.Row;
import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.base.BaseRow;
import com.github.romualdrousseau.any2json.base.BaseTable;
import com.github.romualdrousseau.shuju.strings.StringUtils;
import com.github.romualdrousseau.shuju.types.Tensor;

public class IntelliHeader extends DataTableHeader {

    public IntelliHeader(final BaseHeader header) {
        this(header.getTable(), new BaseCell(header.getName(), header.getColumnIndex(), 1, header.getCell().getRawValue(), header.getTable().getSheet()), false);
        this.setColumnIndex(header.getColumnIndex());
    }

    private IntelliHeader(final BaseTable table, final BaseCell cell, boolean isMeta) {
        super(table, cell);
        this.isMeta = isMeta;
        this.entities = this.sampleEntities();

        final String cellValue = this.getCell().getValue();
        if(StringUtils.isFastBlank(cellValue)) {
            this.name = this.entities().stream().findAny().map(x -> this.getEntitiesAsString()).orElse(DocumentFactory.PIVOT_VALUE_SUFFIX);
        } else if (isMeta) {
            this.name = this.entities().stream().findAny().map(x -> this.getEntitiesAsString()).orElse(cellValue);
        } else {
            this.name = this.getTable().getSheet().getDocument().getModel().toEntityName(cellValue);
        }
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
            if (!curr.isMeta && !buffer.contains(value)) {
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
    public List<String> entities() {
        return this.entities;
    }

    @Override
    public BaseHeader clone() {
        return new IntelliHeader(this);
    }

    @Override
    public boolean hasTag() {
        return this.tag != null;
    }

    @Override
    public HeaderTag getTag() {
        return this.tag;
    }

    @Override
    public BaseTable getTable() {
        return (BaseTable) super.getTable();
    }

    @Override
    public void resetTag() {
        this.tag = null;
        this.nextSibbling = null;
    }

    @Override
    public void updateTag() {
        if (StringUtils.isFastBlank(this.getName())) {
            this.tag = HeaderTag.None;
        } else {
            final String tagValue = this.getTable().getSheet().getDocument().getTagClassifier().predict(this.getName(), this.entities(), this.getTable().getHeaderNames());
            this.tag = new HeaderTag(tagValue);
        }
    }

    public void mergeTo(final IntelliHeader other) {
        IntelliHeader e = this;
        while(e.nextSibbling != null) {
            e = e.nextSibbling;
        }
        e.nextSibbling = other;
    }

    private List<String> sampleEntities() {
        final int N = Math.min(this.getTable().getNumberOfRows(), DocumentFactory.DEFAULT_SAMPLE_COUNT);
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
                n += DocumentFactory.DEFAULT_ENTITY_PROBABILITY;
            }
        }
        if (n > 0.0f) {
            entityVector.if_lt_then(n, 0.0f, 1.0f);
        }
        final List<String> entityList = this.getTable().getSheet().getDocument().getModel().getEntityList();
        return IntStream.range(0, entityVector.size).boxed().filter(i -> entityVector.data[i] == 1).map(i -> entityList.get(i)).toList();
    }

    private final String name;
    private final boolean isMeta;
    private final List<String> entities;
    private HeaderTag tag;
    private IntelliHeader nextSibbling;
}
