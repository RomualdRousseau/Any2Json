package com.github.romualdrousseau.any2json.parser.table;

import java.util.Collections;
import java.util.List;

import com.github.romualdrousseau.any2json.Model;
import com.github.romualdrousseau.any2json.TableParser;
import com.github.romualdrousseau.any2json.base.BaseCell;
import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.any2json.base.BaseTable;
import com.github.romualdrousseau.any2json.base.DataTable;
import com.github.romualdrousseau.any2json.base.MetaTable;
import com.github.romualdrousseau.any2json.header.DataTableHeader;

public class SimpleTableParser implements TableParser {

    public SimpleTableParser(final Model model) {
    }

    @Override
    public void updateModel(final Model model) {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void setParserOptions(final String options) {
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

    private void parseDataTable(final DataTable table) {
        table.getRowAt(0).cells().forEach(c -> table.addHeader(new DataTableHeader(table, (BaseCell) c)));
        table.setFirstRowOffset(1);
    }
}
