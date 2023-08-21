package com.github.romualdrousseau.any2json.intelli;

import java.util.ArrayList;
import java.util.List;

import com.github.romualdrousseau.any2json.Header;
import com.github.romualdrousseau.any2json.Row;
import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.any2json.base.BaseTableGraph;
import com.github.romualdrousseau.any2json.base.DataTable;
import com.github.romualdrousseau.any2json.base.BaseRow;
import com.github.romualdrousseau.any2json.base.RowGroup;
import com.github.romualdrousseau.any2json.header.PivotKeyHeader;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class IntelliTable extends DataTable {

    public IntelliTable(final BaseSheet sheet, final BaseTableGraph root) {
        super(sheet);
        this.buildHeaders(root);
        this.buildTable(root, this.findPivotHeader());
    }

    @Override
    public int getNumberOfColumns() {
        return this.getNumberOfHeaders();
    }

    @Override
    public int getNumberOfRows() {
        return this.rows.size();
    }

    @Override
    public BaseRow getRowAt(final int rowIndex) {
        if (rowIndex < 0 || rowIndex >= getNumberOfRows()) {
            throw new ArrayIndexOutOfBoundsException(rowIndex);
        }
        return this.rows.get(rowIndex);
    }

    @Override
    public void prepareHeaders() {
        for (int i = 0; i < this.tmpHeaders.size(); i++) {
            this.addHeader(this.tmpHeaders.get(i));
        }
        super.prepareHeaders();
    }

    private void buildHeaders(final BaseTableGraph graph) {
        for (final BaseTableGraph child : graph.children()) {
            for (final Header header : child.getTable().headers()) {
                final BaseHeader baseHeader = (BaseHeader) header;
                if (this.checkIfHeaderExists(baseHeader)) {
                    continue;
                }

                BaseHeader newHeader = baseHeader.clone();
                newHeader.setTable(this);
                newHeader.setColumnIndex(this.tmpHeaders.size());
                this.tmpHeaders.add(newHeader);

                if (header instanceof PivotKeyHeader) {
                    newHeader = ((PivotKeyHeader) baseHeader).getPivotValue();
                    newHeader.setTable(this);
                    newHeader.setColumnIndex(this.tmpHeaders.size());
                    this.tmpHeaders.add(newHeader);
                }
            }
        }

        for (final BaseTableGraph child : graph.children()) {
            this.buildHeaders(child);
        }
    }

    private PivotKeyHeader findPivotHeader() {
        PivotKeyHeader result = null;
        for (final Header header : this.tmpHeaders) {
            if (header instanceof PivotKeyHeader) {
                result = (PivotKeyHeader) header;
                break;
            }
        }
        return result;
    }

    private void buildTable(final BaseTableGraph graph, final PivotKeyHeader pivot) {
        for (final BaseTableGraph child : graph.children()) {
            if (child.getTable() instanceof DataTable) {
                this.buildRowsForOneTable(child, (DataTable) child.getTable(), pivot);
            }
        }

        for (final BaseTableGraph child : graph.children()) {
            this.buildTable(child, pivot);
        }
    }

    private void buildRowsForOneTable(final BaseTableGraph graph, final DataTable orgTable, final PivotKeyHeader pivot) {
        if (orgTable.getNumberOfRowGroups() == 0) {
            for (final Row orgRow : orgTable.rows()) {
                final ArrayList<IntelliRow> newRows = buildRowsForOneRow(graph, orgTable, (BaseRow) orgRow, pivot, null);
                this.rows.addAll(newRows);
            }
        } else {
            for (final RowGroup rowGroup : orgTable.rowGroups()) {
                for (int i = 0; i < rowGroup.getNumberOfRows(); i++) {
                    if (rowGroup.getRow() + i >= orgTable.getNumberOfRows()) {
                        break;
                    }
                    final Row orgRow = orgTable.getRowAt(rowGroup.getRow() + i);
                    final ArrayList<IntelliRow> newRows = buildRowsForOneRow(graph, orgTable, (BaseRow) orgRow, pivot,
                            rowGroup);
                    this.rows.addAll(newRows);
                }
            }
        }
    }

    private ArrayList<IntelliRow> buildRowsForOneRow(final BaseTableGraph graph, final DataTable orgTable, final BaseRow orgRow,
            final PivotKeyHeader pivot, final RowGroup rowGroup) {
        final ArrayList<IntelliRow> newRows = new ArrayList<IntelliRow>();

        if(orgRow.isIgnored()) {
            return newRows;
        }

        if (pivot == null) {
            newRows.add(buildOneRow(graph, orgTable, orgRow, null, rowGroup));
        } else {
            for (final BaseCell pivotCell : pivot.getEntries()) {
                if (!StringUtils.isFastBlank(orgRow.getCellAt(pivotCell.getColumnIndex()).getValue())) {
                    newRows.add(buildOneRow(graph, orgTable, orgRow, pivotCell, rowGroup));
                }
            }
        }

        return newRows;
    }

    private IntelliRow buildOneRow(final BaseTableGraph graph, final DataTable orgTable, final BaseRow orgRow, final BaseCell pivotCell,
            final RowGroup rowGroup) {
        final IntelliRow newRow = new IntelliRow(this, this.tmpHeaders.size());

        for (final BaseHeader abstractHeader : this.tmpHeaders) {
            final List<Header> orgHeaders = orgTable.findHeader(abstractHeader);

            if (abstractHeader instanceof PivotKeyHeader && pivotCell != null) {
                if (orgHeaders.size() > 0) {
                    newRow.setCellValue(abstractHeader.getColumnIndex(), pivotCell.getValue(), pivotCell.getRawValue());
                    newRow.setCell(abstractHeader.getColumnIndex() + 1,
                            orgRow.getCellAt(pivotCell.getColumnIndex()));
                }
            } else {
                if (orgHeaders.size() > 0) {
                    for(final Header orgHeader : orgHeaders) {
                        final BaseHeader orgAbstractHeader = (BaseHeader) orgHeader;
                        if (rowGroup == null || !orgAbstractHeader.isRowGroupName()) {
                            newRow.setCell(abstractHeader.getColumnIndex(), orgAbstractHeader.getCellAtRow(orgRow));
                        } else {
                            newRow.setCell(abstractHeader.getColumnIndex(), rowGroup.getCell());
                        }
                    }
                } else {
                    final BaseHeader header = this.findClosestHeaderInGraph(graph.getParent(), abstractHeader);
                    newRow.setCellValue(abstractHeader.getColumnIndex(), header.getValue(), header.getCell().getRawValue());
                }
            }
        }

        return newRow;
    }

    private boolean checkIfHeaderExists(final BaseHeader header) {
        return this.tmpHeaders.contains(header);
    }

    private BaseHeader findClosestHeaderInGraph(final BaseTableGraph graph, final BaseHeader abstractHeader) {
        if (graph == null || graph.getTable() == null) {
            return abstractHeader;
        }

        BaseHeader result = null;
        for(final Header header : graph.getTable().headers()) {
            if (header.equals(abstractHeader)) {
                result = (BaseHeader) header;
            }
        }

        if (result != null) {
            return result;
        }
        else {
            return this.findClosestHeaderInGraph(graph.getParent(), abstractHeader);
        }
    }

    private final ArrayList<BaseHeader> tmpHeaders = new ArrayList<>();
    private final ArrayList<IntelliRow> rows = new ArrayList<>();
}
