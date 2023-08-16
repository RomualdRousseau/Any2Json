package com.github.romualdrousseau.any2json;

import java.util.List;

import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.any2json.base.BaseTable;
import com.github.romualdrousseau.any2json.base.DataTable;
import com.github.romualdrousseau.any2json.base.MetaTable;

public interface TableParser extends AutoCloseable {

    void disablePivot();

    void setParserOptions(String options);

    List<DataTable> getDataTables(BaseSheet sheet, List<BaseTable> tables);

    List<MetaTable> getMetaTables(BaseSheet sheet, List<BaseTable> tables);
}
