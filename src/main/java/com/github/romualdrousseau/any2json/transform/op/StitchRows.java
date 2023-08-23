package com.github.romualdrousseau.any2json.transform.op;

import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.any2json.config.Settings;
import com.github.romualdrousseau.shuju.strings.StringFuzzy;

public class StitchRows {

    public static void Apply(final BaseSheet sheet) {
        for(int i = 0; i <= sheet.getLastRowNum(); i++) {
            if (StitchRows.isStichedRow(sheet, i)) {
                sheet.markRowAsNull(i);
            }
        }
        sheet.removeAllNullRows();
    }

    private static boolean isStichedRow(final BaseSheet sheet, final int rowIndex) {
        if (rowIndex <= 0 || rowIndex >= sheet.getSheetStore().getLastRowNum()) {
            return false;
        }
        final String hash = StitchRows.getRowPattern(sheet, rowIndex);
        // Keep non empty rows
        if (!hash.isEmpty()) {
            return false;
        }
        // Test if the previous and next rows can be "stiched"
        final String hashPrev = StitchRows.getRowPattern(sheet, rowIndex - 1);
        final String hashNext = StitchRows.getRowPattern(sheet, rowIndex + 1);
        return StringFuzzy.Hamming(hashPrev, hashNext) >= Settings.DEFAULT_RATIO_SIMILARITY;
    }

    private static String getRowPattern(final BaseSheet sheet, final int rowIndex) {
        String hash = "";
        int countEmptyCells = 0;
        for (int i = 0; i <= sheet.getSheetStore().getLastColumnNum(rowIndex);) {
            final String value = sheet.getSheetStore().getCellDataAt(i, rowIndex);
            if (value != null) {
                if (value.isEmpty()) {
                    hash += "s";
                    countEmptyCells++;
                }
                else {
                    hash += sheet.getDocument().getModel().toEntityValue(value).map(x -> "e").orElse("v");
                }
            }
            i += sheet.getSheetStore().getNumberOfMergedCellsAt(i, rowIndex);
        }
        if (countEmptyCells == hash.length()) {
            hash = "";
        }
        return hash;
    }
}
