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
import com.github.romualdrousseau.any2json.transform.op.RepeatCell;
import com.github.romualdrousseau.shuju.strings.StringUtils;

/**
 * TransformableSheet Class is responsible to apply transformations to a sheet such as remove columns or rows. The
 * transformations are called through a recipe written in Python.
 */
public class TransformableSheet {

    /**
     * This is a private constructor for the TransformableSheet class to apply transformations to the given sheet.
     *
     * @param sheet the sheet to transform
     */
    private TransformableSheet(final BaseSheet sheet) {
        this.sheet = sheet;
    }

    /**
     * This method returns a new instance of TransformableSheet initialized with the given sheet. the TransformableSheet
     * can be used to apply transformation of the sheet such as remove columns or rows.
     *
     * @param sheet the sheet to transform
     * @return a new instance
     */
    public static TransformableSheet of(final BaseSheet sheet) {
        return new TransformableSheet(sheet);
    }

    /**
     * This method performs all the transformations on the sheet by calling the "autoRecipe" method on the sheet's
     * associated document and executing the custom recipe using a Python interpreter.
     */
    public void applyAll() {
        ((BaseDocument) this.sheet.getDocument()).autoRecipe(this.sheet);

        final String recipe = this.sheet.getDocument().getRecipe();
        if (!StringUtils.isBlank(recipe)) {
            try (PythonInterpreter pyInterp = new PythonInterpreter()) {
                pyInterp.set("sheet", this);
                pyInterp.exec(recipe);
            }
        }
    }

    /**
     * This method sets the parser options for the table parser used by the sheet's associated document.
     *
     * @param options the parser options
     */
    public void setDataTableParserFactory(String options) {
        this.sheet.getDocument().getTableParser().setParserOptions(options);
    }

    /**
     * This method unmerges all merged cells in the sheet.
     */
    public void unmergeAll() {
        this.sheet.unmergeAll();
    }

    /**
     * This method merges the cells in the column specified by the given column index. The value of a given cell is
     * copied to all blank cells below it.
     *
     * @param colIndex the column index
     *
     * @deprecated use {@link TransformableSheet#repeatCell(int)}
     */
    @Deprecated
    public void mergeCell(final int colIndex) {
        RepeatCell.Apply(this.sheet, colIndex);
    }

    /**
     * This method repeat the value for all the cells in the column specified by the given column index.
     * The value of a given cell is copied to all blank cells below it.
     *
     * @param colIndex the column index
     */
    public void repeatCell(final int colIndex) {
        RepeatCell.Apply(this.sheet, colIndex);
    }

    /**
     * This method patches the cells of the given column and row indices with the given value. The style is copied from
     * an existing cell.
     *
     * @param colIndex1 the column index to copy the style from
     * @param rowIndex1 the row index to copy the style from
     * @param colIndex2 the column index to copy the style  to
     * @param rowIndex2 the row index to copy the style  to
     * @param value the value of the destination cell
     */
    public void patchCell(final int colIndex1, final int rowIndex1, final int colIndex2, final int rowIndex2,
            final String value) {
        this.sheet.patchCell(colIndex1, rowIndex1, colIndex2, rowIndex2, value);
    }

    /**
     * This method sets the bitmap threshold for the sheet. The bitmap threshold represents the strength of
     * close elements in a sheet to be combined together.
     *
     * @param bitmapThreshold the bitmap threshold
     */
    public void setBitmapThreshold(final float bitmapThreshold) {
        this.sheet.setBitmapThreshold(bitmapThreshold);
    }

    /**
     * This method disables the pivot functionality of the sheet's associated document.
     */
    public void disablePivot() {
        this.sheet.getDocument().getTableParser().disablePivot();
    }

    /**
     * This method sets the pivot option for the sheet using the given option string.
     *
     * @param option the option string: "NONE", "WITH_TYPE"
     */
    public void setPivotOption(final String option) {
        this.sheet.setPivotOption(Enum.valueOf(PivotOption.class, option));
    }

    /**
     * This method sets the name of the pivot key header for the sheet using the given format.
     *
     * @param format the format used as {@link String#format(String, Object...)}
     */
    public void setPivotKeyFormat(final String format) {
        this.sheet.setPivotKeyFormat(format);
    }

    /**
     * This method sets the name of the pivot type header for the sheet using the given format.
     *
     * @param format the format used as {@link String#format(String, Object...)}
     */
    public void setPivotTypeFormat(final String format) {
        this.sheet.setPivotTypeFormat(format);
    }

    /**
     * This method sets the name of the pivot value header for the sheet using the given format.
     *
     * @param format the format used as {@link String#format(String, Object...)}
     */
    public void setPivotValueFormat(final String format) {
        this.sheet.setPivotValueFormat(format);
    }

    /**
     * This method sets the name of the group header for the sheet using the given format.
     *
     * @param format the format used as String#format(String, Object...)}
     */
    public void setGroupValueFormat(final String format) {
        this.sheet.setGroupValueFormat(format);
    }

    /**
     * This method drops the column specified by the given column index from the sheet.
     *
     * Refrain to use this method as the colIndex is absolute and weak to layout changes.
     *
     * @param colIndex the column index to drop
     */
    public void dropColumn(final int colIndex) {
        DropColumn.Apply(this.sheet, colIndex);
    }

    /**
     * This method drops columns from the sheet that have a fill ratio less than the given fill ratio.
     *
     * @param fillRatio the fill ratio
     *
     *  @deprecated use {@link TransformableSheet#dropColumnsWhenFillRatioLessThan(float)}
     */
    @Deprecated
    public void dropNullColumns(final float fillRatio) {
        DropColumnsWhenFillRatioLessThan.Apply(this.sheet, fillRatio);
    }

    /**
     * This method drops columns from the sheet that have a fill ratio less than the given fill ratio.
     *
     * @param fillRatio the fill ratio
     */
    public void dropColumnsWhenFillRatioLessThan(final float fillRatio) {
        DropColumnsWhenFillRatioLessThan.Apply(this.sheet, fillRatio);
    }

    /**
     * This method drops columns from the sheet that have an entropy less than the given maximum entropy.
     *
     * @param max the maximum entropy
     */
    public void dropColumnsWhenEntropyLessThan(final float max) {
        DropColumnsWhenEntropyLessThan.Apply(this.sheet, max);
    }

    /**
     * This method drops the row specified by the given row index from the sheet.
     *
     * Refrain to use this method as the rowIndex is absolute and weak to layout changes.
     *
     * @param rowIndex the row index to drop
     */
    public void dropRow(final int rowIndex) {
        DropRow.Apply(this.sheet, rowIndex);
    }

    /**
     * This method drops rows from the sheet that have a fill ratio less than the given fill ratio.
     *
     * @param fillRatio the fill ratio
     *
     * @deprecated use {@link TransformableSheet#dropRowsWhenFillRatioLessThan(float)}
     */
    @Deprecated
    public void dropNullRows(final float fillRatio) {
        DropRowsWhenFillRatioLessThan.Apply(this.sheet, fillRatio);
    }

    /**
     * This method drops rows from the sheet that have a fill ratio less than the given fill ratio.
     *
     * @param fillRatio the fill ratio
     */
    public void dropRowsWhenFillRatioLessThan(final float fillRatio) {
        DropRowsWhenFillRatioLessThan.Apply(this.sheet, fillRatio);
    }

    /**
     * This method drops rows from the sheet that have an entropy less than the given maximum entropy.
     *
     * @param max the maximum entropy
     */
    public void dropRowsWhenEntropyLessThan(final float max) {
        DropRowsWhenEntropyLessThan.Apply(this.sheet, max);
    }

    private final BaseSheet sheet;
}
