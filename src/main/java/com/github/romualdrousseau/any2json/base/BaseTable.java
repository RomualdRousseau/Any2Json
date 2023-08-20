package com.github.romualdrousseau.any2json.base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.romualdrousseau.any2json.Header;
import com.github.romualdrousseau.any2json.Row;
import com.github.romualdrousseau.any2json.Table;

public class BaseTable implements Table, Visitable {

    public BaseTable(final BaseSheet sheet, final int firstColumn, final int firstRow, final int lastColumn,
            final int lastRow) {
        this(sheet, firstColumn, firstRow, lastColumn, lastRow, new RowCache(), new LinkedList<>());
    }

    public BaseTable(final BaseTable parent) {
        this(parent.sheet, parent.firstColumn, parent.firstRow, parent.lastColumn, parent.lastRow, parent.cachedRows, parent.ignoreRows);
    }

    public BaseTable(final BaseTable parent, final int firstRow, final int lastRow) {
        this(parent.sheet, parent.firstColumn, firstRow, parent.lastColumn, lastRow, parent.cachedRows, parent.ignoreRows);
    }

    private BaseTable(final BaseSheet sheet, final int firstColumn, final int firstRow, final int lastColumn,
            final int lastRow, RowCache cachedRows, List<Integer> ignoreRows) {
        assert (firstColumn <= lastColumn) : "first column must be before last column";
        assert (firstRow <= lastRow) : "first row must be before last row";

        this.sheet = sheet;
        this.firstColumn = firstColumn;
        this.firstRow = firstRow;
        this.lastColumn = lastColumn;
        this.cachedRows = cachedRows;
        this.ignoreRows = ignoreRows;
        this.headers = new LinkedList<>();

        this.visited = false;
        this.lastRow = lastRow;
        this.firstRowOffset = 0;
        this.lastRowOffset = 0;
        this.headerRowOffset = 0;
        this.loadCompleted = false;
    }

    @Override
    public BaseSheet getSheet() {
        return this.sheet;
    }

    @Override
    public int getNumberOfColumns() {
        return this.lastColumn - this.firstColumn + 1;
    }

    @Override
    public int getNumberOfRows() {
        return (this.lastRow + this.lastRowOffset) - (this.firstRow + this.firstRowOffset) + 1;
    }

    @Override
    public BaseRow getRowAt(final int rowIndex) {
        if (rowIndex < 0 || rowIndex >= getNumberOfRows()) {
            throw new ArrayIndexOutOfBoundsException(rowIndex);
        }

        final int relRowIndex = this.firstRowOffset + rowIndex;

        BaseRow result = cachedRows.get(this.firstRow + relRowIndex);
        if (result == null) {
            result = new BaseRow(this, relRowIndex);

            // Retrieve ignore status possibly lost in cache removal
            for(Integer i: this.ignoreRows()) {
                if (i == rowIndex) {
                    result.setIgnored(true);
                }
            }

            cachedRows.put(this.firstRow + relRowIndex, result);
        }

        return result;
    }

    @Override
    public Iterable<Row> rows() {
        return new RowIterable(this);
    }

    @Override
    public int getNumberOfHeaders() {
        return this.headers.size();
    }

    @Override
    public List<String> getHeaderNames() {
        List<String> result = new ArrayList<String>();
        for(Header header: this.headers()) {
            result.add(header.getName());
        }
        return result;
    }

    @Override
    public BaseHeader getHeaderAt(final int i) {
        return (i < this.headers.size()) ? (BaseHeader) this.headers.get(i) : null;
    }

    @Override
    public Iterable<Header> headers() {
        return this.headers;
    }

    @Override
    public boolean isVisited() {
        return this.visited;
    }

    @Override
    public void setVisited(final boolean flag) {
        this.visited = flag;
    }

    @Override
    public void updateHeaderTags() {
    }

    @Override
    public int getNumberOfHeaderTags() {
        return 0;
    }

    @Override
    public Iterable<Header> headerTags() {
        return null;
    }

    public int getFirstColumn() {
        return this.firstColumn;
    }

    public int getFirstRow() {
        return this.firstRow;
    }

    public int getLastColumn() {
        return this.lastColumn;
    }

    public int getLastRow() {
        return this.lastRow;
    }

    public int getFirstRowOffset() {
        return this.firstRowOffset;
    }

    public void setFirstRowOffset(final int offset) {
        assert (offset >= 0);
        assert (this.lastRowOffset <= offset);
        this.firstRowOffset = offset;
    }

    public int getLastRowOffset() {
        return this.lastRowOffset;
    }

    public void setLastRowOffset(final int offset) {
        assert (offset <= 0);
        assert (offset <= this.firstRowOffset);
        this.lastRowOffset = offset;
    }

    public void adjustLastRow(int lastRow) {
        this.lastRowOffset = -1;
        this.lastRow = lastRow;
    }

    public int getHeaderRowOffset() {
        return this.headerRowOffset;
    }

    public void setHeaderRowOffset(final int offset) {
        assert (offset >= 0);
        assert (this.lastRowOffset <= offset);
        this.headerRowOffset = offset;
    }

    public boolean isLoadCompleted() {
        return this.loadCompleted;
    }

    public void setLoadCompleted(final boolean flag) {
        this.loadCompleted = flag;
    }

    public List<Integer> ignoreRows() {
        return this.ignoreRows;
    }

    public void addHeader(final BaseHeader header) {
        this.headers.addLast(header);
    }

    public void setHeader(final int i, final BaseHeader header) {
        this.headers.set(i, header);
    }

    public List<Header> findHeader(final Header headerToFind) {
        final LinkedList<Header> result = new LinkedList<Header>();
        for (final Header header : this.headers()) {
            if (header.equals(headerToFind)) {
                result.add(header);
            }
        }
        return result;
    }

    private final RowCache cachedRows;
    private final List<Integer> ignoreRows;
    private final BaseSheet sheet;
    private final int firstColumn;
    private final int firstRow;
    private final int lastColumn;
    private final LinkedList<Header> headers;
    private int lastRow;
    private int firstRowOffset;
    private int lastRowOffset;
    private int headerRowOffset;
    private boolean loadCompleted;
    private boolean visited;
}
