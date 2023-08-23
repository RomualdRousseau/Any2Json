package com.github.romualdrousseau.any2json.intelli;

import java.util.ArrayList;
import java.util.List;

import com.github.romualdrousseau.any2json.Header;
import com.github.romualdrousseau.any2json.PivotOption;
import com.github.romualdrousseau.any2json.Row;
import com.github.romualdrousseau.any2json.base.BaseHeader;
import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.any2json.base.BaseTableGraph;
import com.github.romualdrousseau.any2json.base.DataTable;
import com.github.romualdrousseau.any2json.base.BaseRow;
import com.github.romualdrousseau.any2json.base.RowGroup;
import com.github.romualdrousseau.any2json.header.PivotEntry;
import com.github.romualdrousseau.any2json.header.PivotKeyHeader;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class IntelliTable extends DataTable {

    public IntelliTable(final BaseSheet sheet, final BaseTableGraph root) {
        super(sheet);

        // Collect headers

        root.parse(
                e -> e.getTable().headers().forEach(h -> this.addTmpHeader((BaseHeader) h)));

        // Build tables

        final PivotKeyHeader pivot = this.findPivotHeader();
        root.parseIf(
                e -> this.buildRowsForOneTable(e, (DataTable) e.getTable(), pivot),
                e -> e.getTable() instanceof DataTable);
        this.setLoadCompleted(true);

        // Finalize headers

        this.tmpHeaders.forEach(h -> this.addHeader(new IntelliHeader(h)));
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

    private void addTmpHeader(final BaseHeader header) {
        if (this.checkIfHeaderAlreadyBuilt(header)) {
            return;
        }

        BaseHeader newHeader = header.clone();
        newHeader.setTable(this);
        newHeader.setColumnIndex(this.tmpHeaders.size());
        this.tmpHeaders.add(newHeader);

        if (header instanceof PivotKeyHeader) {
            if (this.getSheet().getPivotOption() == PivotOption.WITH_TYPE) {
                newHeader = ((PivotKeyHeader) header).getPivotType();
                newHeader.setTable(this);
                newHeader.setColumnIndex(this.tmpHeaders.size());
                this.tmpHeaders.add(newHeader);
            }

            newHeader = ((PivotKeyHeader) header).getPivotValue();
            newHeader.setTable(this);
            newHeader.setColumnIndex(this.tmpHeaders.size());
            this.tmpHeaders.add(newHeader);
        }
    }

    private void buildRowsForOneTable(final BaseTableGraph graph, final DataTable orgTable,
            final PivotKeyHeader pivot) {
        if (orgTable.getNumberOfRowGroups() == 0) {
            for (final Row orgRow : orgTable.rows()) {
                final List<IntelliRow> newRows = buildRowsForOneRow(graph, orgTable, (BaseRow) orgRow, pivot, null);
                this.rows.addAll(newRows);
            }
        } else {
            for (final RowGroup rowGroup : orgTable.rowGroups()) {
                for (int i = 0; i < rowGroup.getNumberOfRows(); i++) {
                    if (rowGroup.getRow() + i >= orgTable.getNumberOfRows()) {
                        break;
                    }
                    final Row orgRow = orgTable.getRowAt(rowGroup.getRow() + i);
                    final List<IntelliRow> newRows = buildRowsForOneRow(graph, orgTable, (BaseRow) orgRow, pivot,
                            rowGroup);
                    this.rows.addAll(newRows);
                }
            }
        }
    }

    private List<IntelliRow> buildRowsForOneRow(final BaseTableGraph graph, final DataTable orgTable,
            final BaseRow orgRow,
            final PivotKeyHeader pivot, final RowGroup rowGroup) {
        final ArrayList<IntelliRow> newRows = new ArrayList<IntelliRow>();

        if (orgRow.isIgnored()) {
            return newRows;
        }

        if (pivot == null) {
            newRows.add(buildOneRow(graph, orgTable, orgRow, null, rowGroup));
            return newRows;
        }

        for (final PivotEntry pivotEntry: pivot.getEntries()) {
            if (!StringUtils.isFastBlank(orgRow.getCellAt(pivotEntry.getCell().getColumnIndex()).getValue())) {
                newRows.add(buildOneRow(graph, orgTable, orgRow, pivotEntry, rowGroup));
            }
        }
        return newRows;
    }

    private IntelliRow buildOneRow(final BaseTableGraph graph, final DataTable orgTable, final BaseRow orgRow,
            final PivotEntry pivotEntry, final RowGroup rowGroup) {
        final IntelliRow newRow = new IntelliRow(this, this.tmpHeaders.size());

        for (final BaseHeader abstractHeader : this.tmpHeaders) {
            final List<Header> orgHeaders = orgTable.findHeader(abstractHeader);

            if (abstractHeader instanceof PivotKeyHeader && pivotEntry != null) {
                if (orgHeaders.size() > 0) {
                    if (this.getSheet().getPivotOption() == PivotOption.WITH_TYPE) {
                        newRow.setCell(abstractHeader.getColumnIndex(), pivotEntry.getValue(), pivotEntry.getValue());
                        newRow.setCell(abstractHeader.getColumnIndex() + 1, pivotEntry.getTypeValue(), pivotEntry.getTypeValue());
                        newRow.setCell(abstractHeader.getColumnIndex() + 2, orgRow.getCellAt(pivotEntry.getCell().getColumnIndex()));
                    } else {
                        newRow.setCell(abstractHeader.getColumnIndex(), pivotEntry.getCell());
                        newRow.setCell(abstractHeader.getColumnIndex() + 1, orgRow.getCellAt(pivotEntry.getCell().getColumnIndex()));
                    }
                }
            } else {
                if (orgHeaders.size() > 0) {
                    for (final Header orgHeader : orgHeaders) {
                        final BaseHeader orgAbstractHeader = (BaseHeader) orgHeader;
                        if (rowGroup == null || !orgAbstractHeader.hasRowGroup()) {
                            newRow.setCell(abstractHeader.getColumnIndex(), orgAbstractHeader.getCellAtRow(orgRow));
                        } else {
                            newRow.setCell(abstractHeader.getColumnIndex(), rowGroup.getCell());
                        }
                    }
                } else {
                    final BaseHeader header = graph.getParent().findClosestHeader(abstractHeader);
                    newRow.setCell(abstractHeader.getColumnIndex(), header.getValue(), header.getCell().getRawValue());
                }
            }
        }

        return newRow;
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

    private boolean checkIfHeaderAlreadyBuilt(final Header header) {
        return this.tmpHeaders.contains(header);
    }

    private final ArrayList<BaseHeader> tmpHeaders = new ArrayList<>();
    private final ArrayList<IntelliRow> rows = new ArrayList<>();
}
