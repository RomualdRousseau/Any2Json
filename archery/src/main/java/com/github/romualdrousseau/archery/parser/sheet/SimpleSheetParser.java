package com.github.romualdrousseau.archery.parser.sheet;

import java.util.List;

import com.github.romualdrousseau.archery.SheetParser;
import com.github.romualdrousseau.archery.base.BaseSheet;
import com.github.romualdrousseau.archery.base.BaseTable;

public class SimpleSheetParser implements SheetParser {

    @Override
    public List<BaseTable> findAllTables(final BaseSheet sheet) {
        return List.of(new BaseTable(sheet, 0, 0, sheet.getLastColumnNum(), sheet.getLastRowNum()));
    }
}
