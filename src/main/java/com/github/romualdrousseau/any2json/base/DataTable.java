package com.github.romualdrousseau.any2json.base;

import java.util.HashMap;
import java.util.LinkedList;

import com.github.romualdrousseau.any2json.Header;
import com.github.romualdrousseau.any2json.header.DataTableHeader;
import com.github.romualdrousseau.any2json.header.MetaTableHeader;
import com.github.romualdrousseau.any2json.header.PivotKeyHeader;
import com.github.romualdrousseau.any2json.intelli.IntelliHeader;

public class DataTable extends BaseTable {

    public DataTable(final BaseSheet sheet) {
        super(sheet, 0, 0, sheet.getLastColumnNum(), sheet.getLastRowNum());
    }

    public DataTable(final BaseTable table) {
        super(table);
    }

    public DataTable(final BaseTable table, final int rowOffset) {
        super(table, table.getFirstRow() + rowOffset, table.getLastRow());
    }

    public int getNumberOfRowGroups() {
        return this.rowGroups.size();
    }

    public Iterable<RowGroup> rowGroups() {
        return this.rowGroups;
    }

    public void addRowGroup(final RowGroup rowGroup) {
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
    public int getNumberOfHeaderTags() {
        return this.headersByTag.size();
    }

    @Override
    public Iterable<Header> headerTags() {
        return this.headersByTag.values();
    }

    @Override
    public void updateHeaderTags() {
        for (final Header header : this.headers()) {
            ((DataTableHeader) header).resetTag();
        }

        for (final Header header : this.headers()) {
            ((DataTableHeader) header).updateTag();
        }

        for (final Header header : this.headers()) {
            if (header.hasTag() && !header.getTag().isUndefined()) {
                final Header head = this.headersByTag.putIfAbsent(header.getTag().getValue(), header);
                if (head != null && head instanceof IntelliHeader && header instanceof IntelliHeader) {
                    ((IntelliHeader) head).mergeTo((IntelliHeader) header);
                }
            }
        }
    }

    private final HashMap<String, Header> headersByTag = new HashMap<>();
    private final LinkedList<RowGroup> rowGroups = new LinkedList<>();
}
