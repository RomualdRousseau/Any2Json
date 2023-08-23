package com.github.romualdrousseau.any2json.parser.sheet;

import java.util.List;

import com.github.romualdrousseau.any2json.SheetParser;
import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.any2json.base.BaseTable;

public class SimpleSheetParser implements SheetParser {

    @Override
    public List<BaseTable> findAllTables(final BaseSheet sheet) {
        return List.of(new BaseTable(sheet, 0, 0, sheet.getLastColumnNum(), sheet.getLastRowNum()));
    }
}
