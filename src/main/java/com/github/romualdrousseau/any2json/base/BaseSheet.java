package com.github.romualdrousseau.any2json.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.romualdrousseau.any2json.Document;
import com.github.romualdrousseau.any2json.PivotOption;
import com.github.romualdrousseau.any2json.Sheet;
import com.github.romualdrousseau.any2json.SheetEvent;
import com.github.romualdrousseau.any2json.SheetListener;
import com.github.romualdrousseau.any2json.Table;
import com.github.romualdrousseau.any2json.config.Settings;
import com.github.romualdrousseau.any2json.event.AllTablesExtractedEvent;
import com.github.romualdrousseau.any2json.event.DataTableListBuiltEvent;
import com.github.romualdrousseau.any2json.event.MetaTableListBuiltEvent;
import com.github.romualdrousseau.any2json.event.SheetPreparedEvent;
import com.github.romualdrousseau.any2json.event.TableGraphBuiltEvent;
import com.github.romualdrousseau.any2json.event.TableReadyEvent;
import com.github.romualdrousseau.any2json.intelli.IntelliTable;
import com.github.romualdrousseau.any2json.transform.TransformableSheet;
import com.github.romualdrousseau.shuju.commons.CollectionUtils;

public class BaseSheet implements Sheet {

    public BaseSheet(final BaseDocument document, final String name, final SheetStore store) {
        this.document = document;
        this.name = name;
        this.sheetStore = store;
        this.storeLastColumnNum = this.computeLastColumnNum();
        this.columnMask = CollectionUtils.mutableRange(0, this.storeLastColumnNum + 1);
        this.rowMask = CollectionUtils.mutableRange(0, this.sheetStore.getLastRowNum() + 1);

        this.pivotOption = PivotOption.NONE;
        this.pivotKeyFormat = "%s " + Settings.PIVOT_KEY_SUFFIX;
        this.pivotValueFormat = "%s " + Settings.PIVOT_VALUE_SUFFIX;
        this.pivotTypeFormat = "%s " + Settings.PIVOT_TYPE_SUFFIX;
        this.groupValueFormat = "%s " + Settings.GROUP_VALUE_SUFFIX;
    }

    @Override
    public Document getDocument() {
        return this.document;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getLastRowNum() {
        return this.rowMask.size() - 1;
    }

    @Override
    public int getLastColumnNum() {
        return this.columnMask.size() - 1;
    }

    @Override
    public void addSheetListener(final SheetListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public Optional<Table> getTable() {
        if (this.getLastRowNum() <= 0 || this.getLastColumnNum() <= 0) {
            return Optional.empty();
        }

        TransformableSheet.of(this).applyAll();
        if (!this.notifyStepCompleted(new SheetPreparedEvent(this))) {
            return Optional.empty();
        }

        final List<BaseTable> tables = this.getDocument().getSheetParser().findAllTables(this);
        if (!this.notifyStepCompleted(new AllTablesExtractedEvent(this, tables))) {
            return Optional.empty();
        }

        final List<DataTable> dataTables = this.getDocument().getTableParser().getDataTables(this, tables);
        if (!this.notifyStepCompleted(new DataTableListBuiltEvent(this, dataTables))) {
            return Optional.empty();
        }
        if (dataTables.size() == 0) {
            return Optional.empty();
        }

        final List<MetaTable> metaTables = this.getDocument().getTableParser().getMetaTables(this, tables);
        if (!this.notifyStepCompleted(new MetaTableListBuiltEvent(this, metaTables))) {
            return Optional.empty();
        }

        final DataTable table;
        if (this.getDocument().getHints().contains(Document.Hint.INTELLI_LAYOUT)) {
            final BaseTableGraph root = BaseTableGraphBuilder.Build(metaTables, dataTables);
            if (!this.notifyStepCompleted(new TableGraphBuiltEvent(this, root))) {
                return Optional.empty();
            }
            table = new IntelliTable(this, root);
        } else {
            table = dataTables.get(0);
        }
        table.updateHeaderTags();
        this.notifyStepCompleted(new TableReadyEvent(this, table));

        return Optional.of(table);
    }

    public SheetStore getSheetStore() {
        return this.sheetStore;
    }

    public int getLastColumnNum(final int rowIndex) {
        final int translatedRow = this.translateRow(rowIndex);
        if (translatedRow < 0) {
            return -1;
        }
        return this.sheetStore.getLastColumnNum(translatedRow) - (this.storeLastColumnNum - this.columnMask.size() + 1);
    }

    public boolean hasCellDataAt(final int colIndex, final int rowIndex) {
        final int translatedColumn = this.translateColumn(colIndex);
        if (translatedColumn < 0) {
            return false;
        }
        final int translatedRow = this.translateRow(rowIndex);
        if (translatedRow < 0) {
            return false;
        }
        return this.sheetStore.hasCellDataAt(translatedColumn, translatedRow);
    }

    public boolean hasCellDecorationAt(final int colIndex, final int rowIndex) {
        final int translatedColumn = this.translateColumn(colIndex);
        if (translatedColumn < 0) {
            return false;
        }
        final int translatedRow = this.translateRow(rowIndex);
        if (translatedRow < 0) {
            return false;
        }
        return this.sheetStore.hasCellDecorationAt(translatedColumn, translatedRow);
    }

    public String getCellDataAt(final int colIndex, final int rowIndex) {
        final int translatedColumn = this.translateColumn(colIndex);
        if (translatedColumn < 0) {
            return null;
        }
        final int translatedRow = this.translateRow(rowIndex);
        if (translatedRow < 0) {
            return null;
        }
        return this.sheetStore.getCellDataAt(translatedColumn, translatedRow);
    }

    public int getNumberOfMergedCellsAt(final int colIndex, final int rowIndex) {
        if (this.unmergedAll) {
            return 1;
        }
        final int translatedColumn = this.translateColumn(colIndex);
        if (translatedColumn < 0) {
            return 1;
        }
        final int translatedRow = this.translateRow(rowIndex);
        if (translatedRow < 0) {
            return 1;
        }
        return this.sheetStore.getNumberOfMergedCellsAt(translatedColumn, translatedRow);
    }

    public void patchCell(final int colIndex1, final int rowIndex1, final int colIndex2, final int rowIndex2,
            final String value) {
        final int translatedColumn1 = this.translateColumn(colIndex1);
        if (translatedColumn1 < 0) {
            return;
        }
        final int translatedRow1 = this.translateRow(rowIndex1);
        if (translatedRow1 < 0) {
            return;
        }
        final int translatedColumn2 = this.translateColumn(colIndex2);
        if (translatedColumn2 < 0) {
            return;
        }
        final int translatedRow2 = this.translateRow(rowIndex2);
        if (translatedRow2 < 0) {
            return;
        }
        this.sheetStore.patchCell(translatedColumn1, translatedRow1, translatedColumn2, translatedRow2, value, this.unmergedAll);
    }

    public boolean notifyStepCompleted(final SheetEvent e) {
        for (final SheetListener listener : listeners) {
            listener.stepCompleted(e);
        }
        return !e.isCanceled();
    }

    public void markColumnAsNull(final int colIndex) {
        if (colIndex < this.columnMask.size()) {
            this.columnMask.set(colIndex, null);
        }
    }

    public void removeAllNullColumns() {
        this.columnMask.removeIf(i -> i == null);
    }

    public void markRowAsNull(final int rowIndex) {
        if (rowIndex < this.rowMask.size()) {
            this.rowMask.set(rowIndex, null);
        }
    }

    public void removeAllNullRows() {
        this.rowMask.removeIf(i -> i == null);
    }

    public void unmergeAll() {
        this.unmergedAll = true;
    }

    public float getBitmapThreshold() {
        return this.bitmapThreshold;
    }

    public void setBitmapThreshold(final float bitmapThreshold) {
        this.bitmapThreshold = bitmapThreshold;
    }

    public PivotOption getPivotOption() {
        return this.pivotOption;
    }

    public void setPivotOption(final PivotOption option) {
        this.pivotOption = option;
    }

    public String getPivotKeyFormat() {
        return this.pivotKeyFormat;
    }

    public void setPivotKeyFormat(final String format) {
        this.pivotKeyFormat = format;
    }

    public String getPivotTypeFormat() {
        return this.pivotTypeFormat;
    }

    public void setPivotTypeFormat(final String format) {
        this.pivotTypeFormat = format;
    }

    public String getPivotValueFormat() {
        return this.pivotValueFormat;
    }

    public void setPivotValueFormat(final String format) {
        this.pivotValueFormat = format;
    }

    public String getGroupValueFormat() {
        return this.groupValueFormat;
    }

    public void setGroupValueFormat(final String format) {
        this.groupValueFormat = format;
    }

    private int translateColumn(final int colIndex) {
        if (colIndex < 0 || colIndex >= this.columnMask.size()) {
            return -1;
        }
        return this.columnMask.get(colIndex);
    }

    private int translateRow(final int rowIndex) {
        if (rowIndex < 0 || rowIndex >= this.rowMask.size()) {
            return -1;
        }
        return this.rowMask.get(rowIndex);
    }

    private int computeLastColumnNum() {
        if (this.sheetStore.getLastRowNum() < 0) {
            return -1;
        }
        int result = this.sheetStore.getLastColumnNum(0);
        for (int i = 1; i <= Math.min(Settings.DEFAULT_SAMPLE_COUNT, this.sheetStore.getLastRowNum()); i++) {
            result = Math.max(result, this.sheetStore.getLastColumnNum(i));
        }
        return result;
    }

    private final BaseDocument document;
    private final String name;
    private final SheetStore sheetStore;
    private final ArrayList<SheetListener> listeners = new ArrayList<SheetListener>();
    private final List<Integer> rowMask;
    private final List<Integer> columnMask;
    private final int storeLastColumnNum;

    private boolean unmergedAll = false;
    private float bitmapThreshold = 0.5f;
    private PivotOption pivotOption;
    private String pivotKeyFormat;
    private String pivotValueFormat;
    private String pivotTypeFormat;
    private String groupValueFormat;

}
