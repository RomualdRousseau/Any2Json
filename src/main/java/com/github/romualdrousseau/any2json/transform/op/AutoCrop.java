package com.github.romualdrousseau.any2json.transform.op;

import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class AutoCrop {

    public static void Apply(final BaseSheet sheet) {
        AutoCrop._cropLeft(sheet);
        AutoCrop._cropRight(sheet);
        sheet.removeAllNullColumns();

        AutoCrop._cropTop(sheet);
        AutoCrop._cropBottom(sheet);
        sheet.removeAllNullRows();
    }

    public static void _cropLeft(final BaseSheet sheet) {
        for(int j = 0; j <= sheet.getLastColumnNum(); j++) {
            int emptyCount = sheet.getLastRowNum() + 1;
            for(int i = 0; i <= sheet.getLastRowNum(); i++) {
                if(sheet.hasCellDataAt(j, i) && !StringUtils.isFastBlank(sheet.getCellDataAt(j, i))) {
                    emptyCount--;
                }
            }
            final float m = 1.0f - (float) emptyCount / (float) (sheet.getLastRowNum() + 1);
            if (m <= 0) {
                sheet.markColumnAsNull(j);
            } else {
                return;
            }
        }
    }

    public static void _cropRight(final BaseSheet sheet) {
        for(int j = sheet.getLastColumnNum(); j >= 0 ; j--) {
            int emptyCount = sheet.getLastRowNum() + 1;
            for(int i = 0; i <= sheet.getLastRowNum(); i++) {
                if(sheet.hasCellDataAt(j, i) && !StringUtils.isFastBlank(sheet.getCellDataAt(j, i))) {
                    emptyCount--;
                }
            }
            final float m = 1.0f - (float) emptyCount / (float) (sheet.getLastRowNum() + 1);
            if (m <= 0) {
                sheet.markColumnAsNull(j);
            } else {
                return;
            }
        }
    }

    public static void _cropTop(final BaseSheet sheet) {
        for(int i = 0; i <= sheet.getLastRowNum(); i++) {
            int emptyCount = sheet.getLastColumnNum() + 1;
            for(int j = 0; j <= sheet.getLastColumnNum(i); j++) {
                if(sheet.hasCellDataAt(j, i) && !StringUtils.isFastBlank(sheet.getCellDataAt(j, i))) {
                    emptyCount--;
                }
            }
            final float m = 1.0f - (float) emptyCount / (float) (sheet.getLastColumnNum() + 1);
            if (m <= 0) {
                sheet.markRowAsNull(i);
            } else {
                return;
            }
        }
    }

    public static void _cropBottom(final BaseSheet sheet) {
        for(int i = sheet.getLastRowNum(); i >= 0; i--) {
            int emptyCount = sheet.getLastColumnNum() + 1;
            for(int j = 0; j <= sheet.getLastColumnNum(i); j++) {
                if(sheet.hasCellDataAt(j, i) && !StringUtils.isFastBlank(sheet.getCellDataAt(j, i))) {
                    emptyCount--;
                }
            }
            final float m = 1.0f - (float) emptyCount / (float) (sheet.getLastColumnNum() + 1);
            if (m <= 0) {
                sheet.markRowAsNull(i);
            } else {
                return;
            }
        }
    }
}
