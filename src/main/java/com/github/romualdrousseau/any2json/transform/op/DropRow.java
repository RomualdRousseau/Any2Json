package com.github.romualdrousseau.any2json.transform.op;

import com.github.romualdrousseau.any2json.base.BaseSheet;

public class DropRow {

    public static void Apply(final BaseSheet sheet, final int rowIndex) {
        sheet.markRowAsNull(rowIndex);
        sheet.removeAllNullRows();
    }
}
