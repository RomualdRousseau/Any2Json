package com.github.romualdrousseau.any2json.transform.op;

import com.github.romualdrousseau.any2json.base.BaseSheet;

public class RepeatCell {

    public static void Apply(final BaseSheet sheet, final int colIndex) {
        int lastRow = -1;
        for(int i = 0; i <= sheet.getLastRowNum(); i++) {
            if(sheet.hasCellDataAt(colIndex, i)) {
                lastRow = i;
            } else if (lastRow >= 0) {
                sheet.patchCell(colIndex, lastRow, colIndex, i, null);
            }
        }
    }
}
