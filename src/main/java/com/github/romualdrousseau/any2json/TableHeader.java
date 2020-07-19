package com.github.romualdrousseau.any2json;

import java.util.ArrayList;

import com.github.romualdrousseau.shuju.util.StringUtility;
import com.github.romualdrousseau.shuju.DataRow;
import com.github.romualdrousseau.shuju.math.Vector;

public class TableHeader {
    public String getName() {
        return this.name;
    }

    public String getCleanName() {
        if (this.name != null && this.cleanName == null) {
            if (this.classifier != null) {
                this.cleanName = StringUtility.cleanToken(this.classifier.getStopWordList().removeStopWords(this.name));
            } else {
                this.cleanName = StringUtility.cleanToken(this.name);
            }
        }
        return this.cleanName;
    }

    public Vector getWordVector() {
        if (this.classifier != null && this.wordVector == null) {
            this.wordVector = this.classifier.getWordList().word2vec(this.getCleanName());
        }
        return this.wordVector;
    }

    public Vector getEntityVector() {
        if (this.classifier != null && this.entityVector == null) {
            this.entityVector = this.entity2vec(0.8f);
        }
        return this.entityVector;
    }

    public Vector getConflictVector(boolean checkForConflicts) {
        TableHeader[] conflictingHeaders = checkForConflicts ? this.findConflictingHeaders() : null;
        return this.words2vec(conflictingHeaders);
    }

    public TableHeader setName(String name) {
        this.name = name;
        return this;
    }

    public int getColumnIndex() {
        return this.columnIndex;
    }

    public TableHeader setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }

    public int getNumberOfCells() {
        return this.numberOfCells;
    }

    public TableHeader setNumberOfCells(int numberOfCells) {
        this.numberOfCells = numberOfCells;
        return this;
    }

    public boolean hasTag() {
        return this.tag != null;
    }

    public HeaderTag getTag() {
        return this.tag;
    }

    public TableHeader setTag(HeaderTag tag) {
        this.tag = tag;
        return this;
    }

    public ITable getTable() {
        return this.table;
    }

    public TableHeader setTable(ITable table) {
        this.table = table;
        return this;
    }

    public ITagClassifier getTagClassifier() {
        return this.classifier;
    }

    public TableHeader setTagClassifier(ITagClassifier classifier) {
        this.classifier = classifier;
        return this;
    }

    public String toString() {
        return this.name;
    }

    public void resetTag() {
        this.cleanName = null;
        this.wordVector = null;
        this.entityVector = null;
        this.tag = null;
        this.next = null;
    }

    public void updateTag(boolean checkForConflicts) {
        if(StringUtility.isEmpty(this.getCleanName())) {
            this.tag = new HeaderTag(this, "none");
        } else {
            DataRow data = new DataRow().addFeature(this.buildFeature(checkForConflicts));
            String tagValue = classifier.predict(data);
            this.tag = new HeaderTag(this, tagValue);
        }
    }

    public DataRow buildRow(String tagValue, TableHeader[] conflicts, boolean ensureWordsExists) {
        if (ensureWordsExists) {
            this.classifier.getWordList().add(this.getCleanName());
            this.wordVector = null;

            if (conflicts != null) {
                for (TableHeader conflict : conflicts) {
                    this.classifier.getWordList().add(conflict.getCleanName());
                    conflict.wordVector = null;
                }
            }
        }
        return new DataRow().addFeature(this.buildFeature(conflicts))
                .setLabel(this.classifier.getTagList().word2vec(tagValue));
    }

    protected void chain(TableHeader other) {
        this.next = other;
    }

    protected TableHeader next() {
        return this.next;
    }

    private Vector buildFeature(boolean checkForConflicts) {
        final Vector entity2vec = this.getEntityVector();
        final Vector word2vec = this.getWordVector();
        final Vector conflict2vec = this.getConflictVector(checkForConflicts);
        return entity2vec.concat(word2vec).concat(conflict2vec);
    }

    private Vector buildFeature(TableHeader[] conflicts) {
        final Vector entity2vec = this.getEntityVector();
        final Vector word2vec = this.getWordVector();
        final Vector conflict2vec = this.words2vec(conflicts);
        return entity2vec.concat(word2vec).concat(conflict2vec);
    }

    private TableHeader[] findConflictingHeaders() {
        ArrayList<TableHeader> result = new ArrayList<TableHeader>();

        for (TableHeader header : this.table.headers()) {
            if (header != this && header.hasTag() && header.tag.equals(this.tag) && !header.tag.isUndefined()) {
                result.add(header);
            }
        }

        if (result.size() == 0) {
            return null;
        } else {
            return result.toArray(new TableHeader[result.size()]);
        }
    }

    private Vector entity2vec(float p) {
        Vector result = new Vector(this.classifier.getEntityList().getVectorSize());

        int n = 0;
        for (int i = 0; i < Math.min(this.table.getNumberOfRows(), this.classifier.getSampleCount()); i++) {
            IRow row = this.table.getRowAt(i);
            if (row != null) {
                TableCell cell = row.getCell(this);
                if (cell.hasValue() && !cell.getEntityVector().isNull()) {
                    result.add(cell.getEntityVector());
                    n++;
                }
            }
        }

        if (n > 0) {
            result.cond(p * ((float) n), 0.0f, 1.0f);
        }

        return result;
    }

    private Vector words2vec(TableHeader[] headers) {
        Vector result = new Vector(classifier.getWordList().getVectorSize());

        if (headers == null) {
            return result;
        }

        for (TableHeader header : headers) {
            result.add(header.getWordVector());
        }

        return result.constrain(0, 1);
    }

    private String name;
    private String cleanName;
    private Vector wordVector;
    private Vector entityVector;
    private int columnIndex;
    private int numberOfCells;
    private ITagClassifier classifier;
    private HeaderTag tag;
    private ITable table;
    private TableHeader next = null;
}
