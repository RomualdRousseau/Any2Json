package com.github.romualdrousseau.any2json.transform.op;

import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class DropRowsWhenFillRatioLessThan {

    public static void Apply(final BaseSheet sheet, final float max) {
        for(int i = 0; i <= sheet.getLastRowNum(); i++) {
            int emptyCount = sheet.getLastColumnNum() + 1;
            for(int j = 0; j <= sheet.getLastColumnNum(i); j++) {
                if(sheet.hasCellDataAt(j, i) && !StringUtils.isFastBlank(sheet.getCellDataAt(j, i))) {
                    emptyCount--;
                }
            }
            final float m = 1.0f - (float) emptyCount / (float) (sheet.getLastColumnNum() + 1);
            if (m <= max) {
                sheet.markRowAsNull(i);
            }
        }
        sheet.removeAllNullRows();
    }
}
