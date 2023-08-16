package com.github.romualdrousseau.any2json.parser.table;

import java.util.Collections;
import java.util.List;

import com.github.romualdrousseau.any2json.Cell;
import com.github.romualdrousseau.any2json.TableParser;
import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.any2json.base.BaseTable;
import com.github.romualdrousseau.any2json.base.DataTable;
import com.github.romualdrousseau.any2json.base.MetaTable;
import com.github.romualdrousseau.any2json.header.DataTableHeader;
import com.github.romualdrousseau.any2json.header.PivotKeyHeader;

public class SimpleTableParser implements TableParser {

    @Override
    public void close() throws Exception {
    }

    @Override
    public void disablePivot() {
    }

    @Override
    public void setParserOptions(String options) {
    }

    @Override
    public List<DataTable> getDataTables(final BaseSheet sheet, final List<BaseTable> tables) {
        final DataTable dataTable = new DataTable(tables.get(0));
        this.parseDataTable(dataTable);
        return List.of(dataTable);
    }

    @Override
    public List<MetaTable> getMetaTables(final BaseSheet sheet, final List<BaseTable> tables) {
        return Collections.emptyList();
    }

    private void parseDataTable(DataTable table) {
        for (Cell cell : table.getRowAt(0).cells()) {
            BaseCell baseCell = (BaseCell) cell;
            if(baseCell.isPivotHeader()) {
                PivotKeyHeader foundPivot = table.findFirstPivotHeader();
                if (foundPivot == null) {
                    table.addHeader(new PivotKeyHeader(table, baseCell));
                } else {
                    foundPivot.addEntry(baseCell);
                }
            } else {
                table.addHeader(new DataTableHeader(table, baseCell));
            }
        }
        table.setFirstRowOffset(1);
    }
}
