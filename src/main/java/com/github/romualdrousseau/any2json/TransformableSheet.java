package com.github.romualdrousseau.any2json;

import java.util.List;
import java.util.EnumSet;

import org.python.util.PythonInterpreter;

import com.github.romualdrousseau.any2json.base.BaseDocument;
import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.any2json.transform.op.DropColumn;
import com.github.romualdrousseau.any2json.transform.op.DropColumnsWhenEntropyLessThan;
import com.github.romualdrousseau.any2json.transform.op.DropColumnsWhenFillRatioLessThan;
import com.github.romualdrousseau.any2json.transform.op.DropRow;
import com.github.romualdrousseau.any2json.transform.op.DropRowsWhenEntropyLessThan;
import com.github.romualdrousseau.any2json.transform.op.DropRowsWhenFillRatioLessThan;
import com.github.romualdrousseau.any2json.transform.op.RepeatColumnCell;
import com.github.romualdrousseau.any2json.transform.op.RepeatRowCell;
import com.github.romualdrousseau.any2json.transform.op.SwapRows;
import com.github.romualdrousseau.shuju.strings.StringUtils;

/**
 * TransformableSheet Class is responsible to apply transformations to a sheet
 * such as remove columns or rows. The
 * transformations are called through a recipe written in Python.
 */
public class TransformableSheet {

    /**
     * This is a private constructor for the TransformableSheet class to apply
     * transformations to the given sheet.
     *
     * @param sheet the sheet to transform
     */
    private TransformableSheet(final BaseSheet sheet) {
        this.sheet = sheet;
    }

    /**
     * This method returns a new instance of TransformableSheet initialized with the
     * given sheet. the TransformableSheet
     * can be used to apply transformation of the sheet such as remove columns or
     * rows.
     *
     * @param sheet the sheet to transform
     * @return a new instance
     */
    public static TransformableSheet of(final BaseSheet sheet) {
        return new TransformableSheet(sheet);
    }

    /**
     * This method performs all the transformations on the sheet by calling the
     * "autoRecipe" method on the sheet's
     * associated document and executing the custom recipe using a Python
     * interpreter.
     */
    public void applyAll() {
        ((BaseDocument) this.sheet.getDocument()).autoRecipe(this.sheet);

        final var recipe = this.sheet.getDocument().getRecipe();
        if (!StringUtils.isBlank(recipe)) {
            try (var pyInterp = new PythonInterpreter()) {
                pyInterp.set("sheet", this);
                pyInterp.exec(recipe);
            }
        }
    }

    /**
     * This method sets the hints for the associated document by overwriting the
     * default hints for the document format.
     *
     * @param hints the hints: INTELLI_EXTRACT, INTELLI_LAYOUT, INTELLI_TAG
     */
    public void setDocumentHints(final String... hints) {
        ((BaseDocument) this.sheet.getDocument()).setRawHints(
                EnumSet.copyOf(List.of(hints).stream().map(Document.Hint::valueOf).toList()));
    }

    /**
     * This method sets the extraction threshold for the sheet. The extraction
     * threshold represents the strength of close elements to be combined together.
     * With a value of 0, the elements with the smallest area will be extracted.
     *
     * Prerequisities: INTELLI_EXTRACT
     *
     * @param threshold the bitmap threshold
     * @deprecated use {@link TransformableSheet#setCapillarityThreshold(float)}
     */
    @Deprecated
    public void setBitmapThreshold(final float threshold) {
        this.sheet.setCapillarityThreshold(threshold);
    }

    /**
     * This method sets the extraction threshold for the sheet. The extraction
     * threshold represents the strength of close elements to be combined together.
     * With a value of 0, the elements with the smallest area will be extracted.
     *
     * Prerequisities: INTELLI_EXTRACT
     *
     * @param threshold the extraction threshold
     */
    public void setCapillarityThreshold(final float threshold) {
        this.sheet.setCapillarityThreshold(threshold);
    }

    /**
     * This method sets the reading direction. The reading direction controls how
     * the different elements of a sheets are
     * linked together. The reading direction is a reading directional preferences
     * in perception of visual stimuli
     * depending of the cultures and writing systems.
     *
     * By default, the reading direction is set to GutenbergReading (or Left-Right
     * Then Top-Botton, or LRTB, or normal Western reading order).
     *
     * Prerequisities: INTELLI_LAYOUT
     *
     * @param readingDirection the reading direction
     */
    public void setReadingDirection(final ReadingDirection readingDirection) {
        this.sheet.getDocument().setReadingDirection(readingDirection);
    }

     /**
     * This method sets the parser options for the table parser used by the sheet's
     * associated document.
     *
     * Prerequisities: INTELLI_LAYOUT
     *
     * @param options the parser options
     * @deprecated use {@link TransformableSheet#setParserOptions(String)}
     */
    @Deprecated
    public void setDataTableParserFactory(final String options) {
        this.setParserOptions(options);
    }

    /**
     * This method sets the parser options for the table parser used by the sheet's
     * associated document.
     *
     * Prerequisities: INTELLI_LAYOUT
     *
     * @param options the parser options
     */
    public void setParserOptions(final String options) {
        this.sheet.getDocument().getTableParser().setParserOptions(options);
    }

    /**
     * This method sets the classifer case for the tag classifer used by the sheet's
     * associated document.
     *
     * @param tagCase the classifer case: CAMEL, SNAKE, NONE
     */
    public void setClassifierCaseMode(final String tagCase) {
        if ("CAMEL".equals(tagCase)) {
            this.sheet.getDocument().getTagClassifier().setCamelMode(true);
            this.sheet.getDocument().getTagClassifier().setSnakeMode(false);
        } else if ("SNAKE".equals(tagCase)) {
            this.sheet.getDocument().getTagClassifier().setCamelMode(false);
            this.sheet.getDocument().getTagClassifier().setSnakeMode(true);
        } else {
            this.sheet.getDocument().getTagClassifier().setCamelMode(false);
            this.sheet.getDocument().getTagClassifier().setSnakeMode(false);
        }
    }

    /**
     * This method disables auto naming the headers of a table. The table will
     * retain its original name.
     */
    public void disableAutoHeaderName() {
        this.sheet.disableAutoHeaderName();
    }

    /**
     * This method disables auto cropping of a sheets. The auto cropping drops
     * all empty rows and columns on the edges of the sheets.
     */
    public void disableAutoCrop() {
        this.sheet.disableAutoCrop();
    }

    /**
     * This method unmerges all merged cells in the sheet.
     */
    public void unmergeAll() {
        this.sheet.unmergeAll();
    }

    /**
     * This method repeat the value for all the cells in the column specified by the
     * given column index. The value of a given cell is copied to all blank cells below it.
     *
     * @param colIndex the column index
     *
     * @deprecated use {@link TransformableSheet#repeatRowCell(int)}
     */
    @Deprecated
    public void mergeCell(final int colIndex) {
        RepeatColumnCell.Apply(this.sheet, colIndex);
    }

    /**
     * This method repeat the value for all the cells in the column specified by the
     * given column index. The value of a given cell is copied to all blank cells below it.
     *
     * @param colIndex the column index
     */
    public void repeatColumnCell(final int colIndex) {
        RepeatColumnCell.Apply(this.sheet, colIndex);
    }

    /**
     * This method repeat the value for all the cells in the row specified by the
     * given row index. The value of a given cell is copied to all blank cells on the right of it.
     *
     * @param rowIndex the row index
     */
    public void repeatRowCell(final int rowIndex) {
        RepeatRowCell.Apply(this.sheet, rowIndex);
    }

    /**
     * This method patches the cells of the given column and row indices with the
     * given value. The style is copied from an existing cell.
     *
     * @param colIndex1 the column index to copy the style from
     * @param rowIndex1 the row index to copy the style from
     * @param colIndex2 the column index to copy the style to
     * @param rowIndex2 the row index to copy the style to
     * @param value     the value of the destination cell
     */
    public void patchCell(final int colIndex1, final int rowIndex1, final int colIndex2, final int rowIndex2,
            final String value) {
        this.sheet.patchCell(colIndex1, rowIndex1, colIndex2, rowIndex2, value);
    }

    /**
     * This method disables the pivot functionality of the sheet's associated
     * document.
     */
    public void disablePivot() {
        this.sheet.disablePivot();
    }

    /**
     * This method sets the pivot entities for the sheet using the given list of
     * entities.
     *
     * @param pivotEntityList the list of entities as a list of string
     */
    public void setPivotEntityList(final List<String> pivotEntityList) {
        this.sheet.setPivotEntityList(pivotEntityList);
    }

    /**
     * This method sets the pivot option for the sheet using the given option
     * string.
     *
     * @param option the option string: "NONE", "WITH_TYPE", "WITH_TYPE_AND_VALUE"
     */
    public void setPivotOption(final String option) {
        this.sheet.setPivotOption(Enum.valueOf(PivotOption.class, option));
    }

    /**
     * This method sets the name of the pivot key header for the sheet using the
     * given format.
     *
     * @param format the format used as {@link String#format(String, Object...)}
     */
    public void setPivotKeyFormat(final String format) {
        this.sheet.setPivotKeyFormat(format);
    }

    /**
     * This method sets the name of the pivot type header for the sheet using the
     * given format.
     *
     * @param format the format used as {@link String#format(String, Object...)}
     */
    public void setPivotTypeFormat(final String format) {
        this.sheet.setPivotTypeFormat(format);
    }

    /**
     * This method sets the name of the pivot value header for the sheet using the
     * given format.
     *
     * @param format the format used as {@link String#format(String, Object...)}
     */
    public void setPivotValueFormat(final String format) {
        this.sheet.setPivotValueFormat(format);
    }

    /**
     * This method sets the name of the group header for the sheet using the given
     * format.
     *
     * @param format the format used as String#format(String, Object...)}
     */
    public void setGroupValueFormat(final String format) {
        this.sheet.setGroupValueFormat(format);
    }

    /**
     * This method drops the column specified by the given column index from the
     * sheet.
     *
     * Refrain to use this method as the colIndex is absolute and weak to layout
     * changes.
     *
     * @param colIndex the column index to drop
     */
    public void dropColumn(final int colIndex) {
        DropColumn.Apply(this.sheet, colIndex);
    }

    /**
     * This method drops columns from the sheet that have a fill ratio less than the
     * given fill ratio.
     *
     * @param fillRatio the fill ratio
     *
     * @deprecated use
     *             {@link TransformableSheet#dropColumnsWhenFillRatioLessThan(float)}
     */
    @Deprecated
    public void dropNullColumns(final float fillRatio) {
        DropColumnsWhenFillRatioLessThan.Apply(this.sheet, fillRatio);
    }

    /**
     * This method drops columns from the sheet that have a fill ratio less than the
     * given fill ratio.
     *
     * @param fillRatio the fill ratio
     */
    public void dropColumnsWhenFillRatioLessThan(final float fillRatio) {
        DropColumnsWhenFillRatioLessThan.Apply(this.sheet, fillRatio);
    }

    /**
     * This method drops columns from the sheet that have an entropy less than the
     * given maximum entropy.
     *
     * @param max the maximum entropy
     */
    public void dropColumnsWhenEntropyLessThan(final float max) {
        DropColumnsWhenEntropyLessThan.Apply(this.sheet, max);
    }

    /**
     * This method drops the row specified by the given row index from the sheet.
     *
     * Refrain to use this method as the rowIndex is absolute and weak to layout
     * changes.
     *
     * @param rowIndex the row index to drop
     */
    public void dropRow(final int rowIndex) {
        DropRow.Apply(this.sheet, rowIndex);
    }

    /**
     * This method drops rows from the sheet that have a fill ratio less than the
     * given fill ratio.
     *
     * @param fillRatio the fill ratio
     *
     * @deprecated use
     *             {@link TransformableSheet#dropRowsWhenFillRatioLessThan(float)}
     */
    @Deprecated
    public void dropNullRows(final float fillRatio) {
        DropRowsWhenFillRatioLessThan.Apply(this.sheet, fillRatio);
    }

    /**
     * This method drops rows from the sheet that have a fill ratio less than the
     * given fill ratio.
     *
     * @param fillRatio the fill ratio
     */
    public void dropRowsWhenFillRatioLessThan(final float fillRatio) {
        DropRowsWhenFillRatioLessThan.Apply(this.sheet, fillRatio);
    }

    /**
     * This method drops rows from the sheet that have a fill ratio less than the
     * given fill ratio.
     *
     * @param fillRatio the fill ratio
     * @param start     the start column
     * @param stop      the stop column
     */
    public void dropRowsWhenFillRatioLessThan(final float fillRatio, final int start, final int stop) {
        DropRowsWhenFillRatioLessThan.Apply(this.sheet, fillRatio, start, stop);
    }

    /**
     * This method drops rows from the sheet that have an entropy less than the
     * given maximum entropy.
     *
     * @param max the maximum entropy
     */
    public void dropRowsWhenEntropyLessThan(final float max) {
        DropRowsWhenEntropyLessThan.Apply(this.sheet, max);
    }

    /**
     * This method drops rows from the sheet that have an entropy less than the
     * given maximum entropy.
     *
     * @param max   the maximum entropy
     * @param start the start column
     * @param stop  the stop column
     */
    public void dropRowsWhenEntropyLessThan(final float max, final int start, final int stop) {
        DropRowsWhenEntropyLessThan.Apply(this.sheet, max, start, stop);
    }

    /**
     * This method swap 2 rows from the sheet.
     *
     * @param rowIndex1 the index of row1
     * @param rowIndex2 the index of row2
     */
    public void swapRows(final int rowIndex1, final int rowIndex2) {
        SwapRows.Apply(this.sheet, rowIndex1, rowIndex2);
    }

    private final BaseSheet sheet;
}
