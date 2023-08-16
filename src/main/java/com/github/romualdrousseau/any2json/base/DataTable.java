package com.github.romualdrousseau.any2json.base;

import java.util.HashMap;
import java.util.LinkedList;

import com.github.romualdrousseau.any2json.Document;
import com.github.romualdrousseau.any2json.Header;
import com.github.romualdrousseau.any2json.header.CompositeHeader;
import com.github.romualdrousseau.any2json.header.IntelliHeader;
import com.github.romualdrousseau.any2json.header.MetaTableHeader;
import com.github.romualdrousseau.any2json.header.PivotKeyHeader;
import com.github.romualdrousseau.any2json.header.SimpleHeader;

public class DataTable extends BaseTable {

    public DataTable(final BaseSheet sheet) {
        super(sheet, 0, 0, 0, 0);
    }

    public DataTable(BaseTable table) {
        super(table);
    }

    public int getNumberOfRowGroups() {
        return this.rowGroups.size();
    }

    public Iterable<RowGroup> rowGroups() {
        return this.rowGroups;
    }

    public void addRowGroup(RowGroup rowGroup) {
        this.rowGroups.add(rowGroup);
    }

    public MetaTableHeader findFirstMetaTableHeader() {
        MetaTableHeader result = null;
        for (final Header header : this.headers()) {
            if (header instanceof MetaTableHeader) {
                result = (MetaTableHeader) header;
                break;
            }
        }
		return result;
	}

    public PivotKeyHeader findFirstPivotHeader() {
        PivotKeyHeader result = null;
        for (final Header header : this.headers()) {
            if (header instanceof PivotKeyHeader) {
                result = (PivotKeyHeader) header;
                break;
            }
        }
		return result;
	}

    @Override
    public void updateHeaderTags() {
        for (Header header : this.headers()) {
            ((IntelliHeader) header).resetTag();
        }

        for (Header header : this.headers()) {
            ((IntelliHeader) header).updateTag();
        }

        for (Header header : this.headers()) {
            if (header.hasTag() && !header.getTag().isUndefined()) {
                Header head = this.headersByTag.putIfAbsent(header.getTag().getValue(), header);
                if (head != null) {
                    ((IntelliHeader) head).mergeTo((IntelliHeader) header);
                }
            }
        }
    }

    @Override
    public int getNumberOfHeaderTags() {
        return this.headersByTag.size();
    }

    @Override
    public Iterable<Header> headerTags() {
        return this.headersByTag.values();
    }

    public void prepareHeaders() {
        this.setLoadCompleted(true); // Give chance to pivot header value to update their name
        if (this.getSheet().getDocument().getHints().contains(Document.Hint.INTELLI_TAG)) {
            for (int i = 0; i < this.getNumberOfHeaders(); i++) {
                this.setHeader(i, new IntelliHeader((CompositeHeader) this.getHeaderAt(i)));
            }
        } else {
            for (int i = 0; i < this.getNumberOfHeaders(); i++) {
                this.setHeader(i, new SimpleHeader(this.getHeaderAt(i)));
            }
        }
    }

    private final HashMap<String, Header> headersByTag = new HashMap<String, Header>();

    private final LinkedList<RowGroup> rowGroups = new LinkedList<RowGroup>();
}
