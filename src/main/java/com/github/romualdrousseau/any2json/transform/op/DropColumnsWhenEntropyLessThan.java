package com.github.romualdrousseau.any2json.transform.op;

import java.util.HashMap;
import java.util.Map.Entry;

import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class DropColumnsWhenEntropyLessThan {

    public static void Apply(final BaseSheet sheet, final float max) {
        for(int j = 0; j <= sheet.getLastColumnNum(); j++) {
            final HashMap<String, Double> x = new HashMap<>();
            int n = 0;
            for(int i = 0; i <= sheet.getLastRowNum(); i++) {
                if(sheet.hasCellDataAt(j, i)) {
                    final String value = sheet.getCellDataAt(j, i);
                    if (!StringUtils.isFastBlank(value)) {
                        x.put(value, x.getOrDefault(value, 0.0) + 1.0);
                        n++;
                    }
                }
            }
            final float e = (float) computeEntropy(x, n);
            if (e <= max) {
                sheet.markColumnAsNull(j);
            }
        }
        sheet.removeAllNullColumns();
    }

    private static double computeEntropy(HashMap<String, Double> x, double n) {
        double result = 0.0f;
        for (final Entry<String, Double> e: x.entrySet()) {
            double p = e.getValue() / n;
            result += p * Math.log(p) / Math.log(2);
        }
        return -result;
    }
}
