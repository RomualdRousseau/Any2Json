package com.github.romualdrousseau.any2json.intelli;

import java.util.LinkedList;

import com.github.romualdrousseau.any2json.Cell;
import com.github.romualdrousseau.any2json.Header;
import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.RowGroup;
import com.github.romualdrousseau.any2json.base.TableMatcher;
import com.github.romualdrousseau.any2json.intelli.header.DataTableHeader;
import com.github.romualdrousseau.any2json.intelli.header.MetaTableHeader;
import com.github.romualdrousseau.any2json.intelli.header.PivotKeyHeader;
import com.github.romualdrousseau.any2json.util.TableLexer;

public class DataTable extends CompositeTable {

    public DataTable(CompositeTable table) {
        super(table);
        this.buildIntelliTable();
    }

    public DataTable(CompositeTable table, TableMatcher layex) {
        super(table);
        this.dataTableContext = new DataTableContext(this);
        layex.match(new TableLexer(table), this.dataTableContext);
        if (this.dataTableContext.getSplitRows().size() > 0) {
            this.adjustLastRow(table.getFirstRow() + this.dataTableContext.getSplitRows().get(0) - 1, true);
        }
        this.setLoadCompleted(true);
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

    public DataTableContext getContext() {
		return this.dataTableContext;
    }

    private void buildIntelliTable() {
        for (Cell cell : this.getRowAt(0).cells()) {
            BaseCell baseCell = (BaseCell) cell;
            if(baseCell.isPivotHeader()) {
                this.addHeader(new PivotKeyHeader(this, baseCell));
            } else {
                this.addHeader(new DataTableHeader(this, baseCell));
            }
        }
        this.setFirstRowOffset(1);
    }

    private final LinkedList<RowGroup> rowGroups = new LinkedList<RowGroup>();
    private DataTableContext dataTableContext;
}
