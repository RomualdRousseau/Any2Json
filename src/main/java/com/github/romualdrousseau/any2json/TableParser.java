package com.github.romualdrousseau.any2json;

import java.util.List;

import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.any2json.base.BaseTable;
import com.github.romualdrousseau.any2json.base.DataTable;
import com.github.romualdrousseau.any2json.base.MetaTable;

public interface TableParser extends AutoCloseable {

    Model getModel();

    TableParser setModel(final Model model);

    String getParserOptions();

    TableParser setParserOptions(final String options);

    List<DataTable> getDataTables(final BaseSheet sheet, final List<BaseTable> tables);

    List<MetaTable> getMetaTables(final BaseSheet sheet, final List<BaseTable> tables);
}
