package com.github.romualdrousseau.any2json.transform.op;

import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class DropColumnsWhenFillRatioLessThan {

    public static void Apply(final BaseSheet sheet, final float max) {
        for(int j = 0; j <= sheet.getLastColumnNum(); j++) {
            int emptyCount = sheet.getLastRowNum() + 1;
            for(int i = 0; i <= sheet.getLastRowNum(); i++) {
                if(sheet.hasCellDataAt(j, i) && !StringUtils.isFastBlank(sheet.getCellDataAt(j, i))) {
                    emptyCount--;
                }
            }
            final float m = 1.0f - (float) emptyCount / (float) (sheet.getLastRowNum() + 1);
            if (m <= max) {
                sheet.markColumnAsNull(j);
            }
        }
        sheet.removeAllNullColumns();
    }
}
