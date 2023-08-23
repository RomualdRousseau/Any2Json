package com.github.romualdrousseau.any2json.transform;

import org.python.util.PythonInterpreter;

import com.github.romualdrousseau.any2json.PivotOption;
import com.github.romualdrousseau.any2json.base.BaseDocument;
import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.any2json.transform.op.DropColumn;
import com.github.romualdrousseau.any2json.transform.op.DropColumnsWhenEntropyLessThan;
import com.github.romualdrousseau.any2json.transform.op.DropColumnsWhenFillRatioLessThan;
import com.github.romualdrousseau.any2json.transform.op.DropRow;
import com.github.romualdrousseau.any2json.transform.op.DropRowsWhenEntropyLessThan;
import com.github.romualdrousseau.any2json.transform.op.DropRowsWhenFillRatioLessThan;
import com.github.romualdrousseau.any2json.transform.op.MergeCell;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class TransformableSheet {

    private TransformableSheet(final BaseSheet sheet) {
        this.sheet = sheet;
    }

    public static TransformableSheet of(final BaseSheet sheet) {
        return new TransformableSheet(sheet);
    }

    public void transformSheet() {
        ((BaseDocument) this.sheet.getDocument()).autoRecipe(this.sheet);

        final String recipe = this.sheet.getDocument().getRecipe();
        if (!StringUtils.isBlank(recipe)) {
            try (PythonInterpreter pyInterp = new PythonInterpreter()) {
                pyInterp.set("sheet", this);
                pyInterp.exec(recipe);
            }
        }
    }

    public void disablePivot() {
        this.sheet.getDocument().getTableParser().disablePivot();
    }

    public void setDataTableParserFactory(String options) {
        this.sheet.getDocument().getTableParser().setParserOptions(options);
    }

    public void unmergeAll() {
        this.sheet.unmergeAll();
    }

    public void mergeCell(final int colIndex) {
        MergeCell.Apply(this.sheet, colIndex);
    }

    public void patchCell(final int colIndex1, final int rowIndex1, final int colIndex2, final int rowIndex2,
            final String value) {
        this.sheet.patchCell(colIndex1, rowIndex1, colIndex2, rowIndex2, value);
    }

    public void setBitmapThreshold(final float bitmapThreshold) {
        this.sheet.setBitmapThreshold(bitmapThreshold);
    }

    public void setPivotOption(final String option) {
        this.sheet.setPivotOption(Enum.valueOf(PivotOption.class, option));
    }

    public void setPivotKeyFormat(final String format) {
        this.sheet.setPivotKeyFormat(format);
    }

    public void setPivotTypeFormat(final String format) {
        this.sheet.setPivotTypeFormat(format);
    }

    public void setPivotValueFormat(final String format) {
        this.sheet.setPivotValueFormat(format);
    }

    public void setGroupValueFormat(final String format) {
        this.sheet.setGroupValueFormat(format);
    }

    public void dropColumn(final int colIndex) {
        DropColumn.Apply(this.sheet, colIndex);
    }

    public void dropNullColumns(final float fillRatio) {
        DropColumnsWhenFillRatioLessThan.Apply(this.sheet, fillRatio);
    }

    public void dropColumnsWhenFillRatioLessThan(final float max) {
        DropColumnsWhenFillRatioLessThan.Apply(this.sheet, max);
    }

    public void dropColumnsWhenEntropyLessThan(final float max) {
        DropColumnsWhenEntropyLessThan.Apply(this.sheet, max);
    }

    public void dropRow(final int rowIndex) {
        DropRow.Apply(this.sheet, rowIndex);
    }

    public void dropNullRows(final float fillRatio) {
        DropRowsWhenFillRatioLessThan.Apply(this.sheet, fillRatio);
    }

    public void dropRowsWhenFillRatioLessThan(final float max) {
        DropRowsWhenFillRatioLessThan.Apply(this.sheet, max);
    }

    public void dropRowsWhenEntropyLessThan(final float max) {
        DropRowsWhenEntropyLessThan.Apply(this.sheet, max);
    }

    private final BaseSheet sheet;
}
