package com.github.romualdrousseau.any2json.transform.op;

import com.github.romualdrousseau.any2json.base.BaseSheet;

public class DropColumn {

    public static void Apply(final BaseSheet sheet, final int colIndex) {
        sheet.markColumnAsNull(colIndex);
        sheet.removeAllNullColumns();
    }
}
