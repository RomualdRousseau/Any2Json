package com.github.romualdrousseau.any2json.transform.op;

import com.github.romualdrousseau.any2json.base.BaseSheet;

public class RepeatRowCell {

    public static void Apply(final BaseSheet sheet, final int rowIndex) {
        int lastColumn = -1;
        for(int i = 0; i <= sheet.getLastColumnNum(rowIndex); i++) {
            if(sheet.hasCellDataAt(i, rowIndex) && !sheet.getCellDataAt(i, rowIndex).isBlank()) {
                lastColumn = i;
            } else if (lastColumn >= 0) {
                sheet.patchCell(lastColumn, rowIndex, i, rowIndex, null);
            }
        }
    }
}
