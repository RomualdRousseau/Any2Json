package com.github.romualdrousseau.any2json.intelli.header;

import com.github.romualdrousseau.any2json.base.BaseRow;
import com.github.romualdrousseau.any2json.intelli.CompositeTable;
import com.github.romualdrousseau.shuju.DataRow;
import com.github.romualdrousseau.shuju.math.Tensor1D;
import com.github.romualdrousseau.shuju.util.StringUtility;

import java.util.List;

import com.github.romualdrousseau.any2json.DocumentFactory;
import com.github.romualdrousseau.any2json.Header;
import com.github.romualdrousseau.any2json.HeaderTag;
import com.github.romualdrousseau.any2json.Row;
import com.github.romualdrousseau.any2json.base.BaseCell;

public class IntelliHeader extends CompositeHeader {

    public IntelliHeader(final CompositeTable table, final BaseCell cell) {
        super(table, cell);
        this.isMeta = false;
    }

    public IntelliHeader(final CompositeHeader header) {
        super(header.getTable(), new BaseCell(header.getName(), header.getColumnIndex(), 1, header.getTable().getClassifier()));
        this.setColumnIndex(header.getColumnIndex());
        this.isMeta = header instanceof MetaHeader;
    }

    private IntelliHeader(final IntelliHeader parent) {
        this(parent.getTable(), parent.getCell());
    }

    @Override
    public String getName() {
        if (this.name == null) {
            final String v1 = this.getCell().getValue();
            this.name = this.getTable().getClassifier().getStopWordList().removeStopWords(v1);
            if(this.name.isEmpty()) {
                final Tensor1D v = this.getEntityVector();
                if(v.sparsity() < 1.0f) {
                    this.name = this.getTable().getClassifier().getEntityList().get(v.argmax());
                } else {
                    this.name = "#VALUE?";
                }
            }
        }
        return this.name;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public BaseCell getCellAtRow(final Row row, boolean merged) {
        if(!merged || this.nextSibbling == null) {
            return this.getCellAtRow(row);
        }

        String buffer = "";

        IntelliHeader curr = this;
        while (curr != null) {
            String value = curr.getCellAtRow(row).getValue();
            if (!curr.isMeta && !buffer.contains(value)) {
                buffer += value;
            }
            curr = curr.nextSibbling;
        }

        if(buffer.isEmpty()) {
            return this.getCellAtRow(row);
        } else {
            return new BaseCell(buffer, this.getColumnIndex(), 1, this.getTable().getClassifier());
        }
    }

    @Override
    public String getEntityString() {
        String result = "";
        boolean firstValue = true;
        for (int i = 0; i < this.getTable().getClassifier().getEntityList().size(); i++) {
            if (this.getEntityVector().get(i) == 1) {
                if (firstValue) {
                    result = this.getTable().getClassifier().getEntityList().get(i);
                    firstValue = false;
                } else {
                    result += "|" + this.getTable().getClassifier().getEntityList().get(i);
                }
            }
        }
        return result;
    }

    @Override
    public CompositeHeader clone() {
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
    public DataRow buildTrainingRow(final String tagValue, final boolean ensureWordsExists) {
        List<Header> others = this.getTable().findOtherHeaders(this);

        if (ensureWordsExists) {
            this.ensureWordExist();
            this.wordVector = null;
            for (final Header other : others) {
                ((IntelliHeader) other).ensureWordExist();
                ((IntelliHeader) other).wordVector = null;
            }
        }

        Tensor1D label = this.getTable().getClassifier().getTagList().word2vec(tagValue);
        return new DataRow()
            .addFeature(this.getEntityVector())
            .addFeature(this.getWordVector())
            .addFeature(this.getOthersVector(others))
            .setLabel(label);
    }

    @Override
    public CompositeTable getTable() {
        return (CompositeTable) super.getTable();
    }

    public void resetTag() {
        this.entityVector = null;
        this.wordVector = null;
        this.tag = null;
        this.nextSibbling = null;
    }

    public void updateTag(final List<Header> others) {
        if (StringUtility.isFastEmpty(this.getName())) {
            this.tag = HeaderTag.None;
        } else {
            final DataRow data = new DataRow()
                .addFeature(this.getEntityVector())
                .addFeature(this.getWordVector())
                .addFeature(this.getOthersVector(others));
            final String tagValue = this.getTable().getClassifier().predict(data);
            this.tag = new HeaderTag(tagValue);
        }
    }

    public void mergeTo(final IntelliHeader other) {
        this.nextSibbling = other;
    }

    private Tensor1D getEntityVector() {
        if (this.entityVector == null) {
            this.entityVector = this.buildEntityVector();
        }
        return this.entityVector;
    }

    private Tensor1D getWordVector() {
        if (this.wordVector == null) {
            this.wordVector = this.buildWordVector();
        }
        return this.wordVector;
    }

    private Tensor1D getOthersVector(final List<Header> others) {
        final Tensor1D result = new Tensor1D(this.getTable().getClassifier().getWordList().getVectorSize());

        if (others == null) {
            return result;
        }

        for (final Header other : others) {
            result.add(((IntelliHeader) other).getWordVector());
        }

        return result.constrain(0, 1);
    }

    private Tensor1D buildEntityVector() {
        final Tensor1D result = new Tensor1D(this.getTable().getClassifier().getEntityList().getVectorSize());

        int n = 0;
        for (int i = 0; i < Math.min(this.getTable().getNumberOfRows(),
                this.getTable().getClassifier().getSampleCount()); i++) {
            final BaseRow row = this.getTable().getRowAt(i);
            if (row == null) {
                continue;
            }

            final BaseCell cell = row.getCellAt(this.getColumnIndex());
            if (cell.hasValue() && !cell.getEntityVector().isNull()) {
                result.add(cell.getEntityVector());
                n++;
            }
        }

        if (n > 0) {
            result.if_lt_then(DocumentFactory.DEFAULT_ENTITY_PROBABILITY * ((float) n), 0.0f, 1.0f);
        }

        return result;
    }

    private Tensor1D buildWordVector() {
        return this.getTable().getClassifier().getWordList().word2vec(this.getName());
    }

    private void ensureWordExist() {
        this.getTable().getClassifier().getWordList().add(this.getName());
    }

    // private Tensor1D buildFeature(final List<Header> others) {
    //     final Tensor1D entity2vec = this.getEntityVector();
    //     final Tensor1D word2vec = this.getWordVector();
    //     final Tensor1D conflict2vec = this.getOthersVector(others);
    //     return entity2vec.concat(word2vec).concat(conflict2vec);
    // }

    private String name;
    private Tensor1D entityVector;
    private Tensor1D wordVector;
    private HeaderTag tag;
    private IntelliHeader nextSibbling;
    private boolean isMeta;
}
