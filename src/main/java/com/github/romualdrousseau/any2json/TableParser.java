package com.github.romualdrousseau.any2json;

import java.util.List;

import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.any2json.base.BaseTable;
import com.github.romualdrousseau.any2json.base.DataTable;
import com.github.romualdrousseau.any2json.base.MetaTable;

public interface TableParser extends AutoCloseable {

    void updateModel(final Model model);

    void disablePivot();

    void setParserOptions(final String options);

    List<DataTable> getDataTables(final BaseSheet sheet, final List<BaseTable> tables);

    List<MetaTable> getMetaTables(final BaseSheet sheet, final List<BaseTable> tables);
}
