package com.github.romualdrousseau.any2json.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.romualdrousseau.any2json.Document;
import com.github.romualdrousseau.any2json.PivotOption;
import com.github.romualdrousseau.any2json.Sheet;
import com.github.romualdrousseau.any2json.SheetEvent;
import com.github.romualdrousseau.any2json.SheetListener;
import com.github.romualdrousseau.any2json.Table;
import com.github.romualdrousseau.any2json.TableGraph;
import com.github.romualdrousseau.any2json.config.Settings;
import com.github.romualdrousseau.any2json.event.AllTablesExtractedEvent;
import com.github.romualdrousseau.any2json.event.DataTableListBuiltEvent;
import com.github.romualdrousseau.any2json.event.MetaTableListBuiltEvent;
import com.github.romualdrousseau.any2json.event.SheetPreparedEvent;
import com.github.romualdrousseau.any2json.event.TableGraphBuiltEvent;
import com.github.romualdrousseau.any2json.event.TableReadyEvent;
import com.github.romualdrousseau.any2json.intelli.IntelliTable;
import com.github.romualdrousseau.any2json.TransformableSheet;
import com.github.romualdrousseau.any2json.transform.op.AutoCrop;
import com.github.romualdrousseau.shuju.commons.CollectionUtils;

public class BaseSheet implements Sheet {

    public BaseSheet(final BaseDocument document, final String name, final PatcheableSheetStore store) {
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
        this.pivotEnabled = true;
        this.autoCropEnabled = true;
        this.autoHeaderNameEnabled = true;
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
    public Optional<TableGraph> getTableGraph() {

        // Here is the core of the algorithm

        if (this.getLastRowNum() <= 0 || this.getLastColumnNum() <= 0) {
            return Optional.empty();
        }

        // Apply recipes

        TransformableSheet.of(this).applyAll();
        if (!this.notifyStepCompleted(new SheetPreparedEvent(this))) {
            return Optional.empty();
        }

        // Auto crop

        if (this.autoCropEnabled) {
            AutoCrop.Apply(this);
        }

        this.document.updateParsersAndClassifiers();

        // Find datatables and metatables

        final var tables = this.document.getSheetParser().findAllTables(this);
        if (!this.notifyStepCompleted(new AllTablesExtractedEvent(this, tables))) {
            return Optional.empty();
        }

        final var dataTables = this.document.getTableParser().getDataTables(this, tables);
        if (!this.notifyStepCompleted(new DataTableListBuiltEvent(this, dataTables))) {
            return Optional.empty();
        }
        if (dataTables.size() == 0) {
            return Optional.empty();
        }

        final var metaTables = this.document.getTableParser().getMetaTables(this, tables);
        if (!this.notifyStepCompleted(new MetaTableListBuiltEvent(this, metaTables))) {
            return Optional.empty();
        }

        if (!this.document.getHints().contains(Document.Hint.INTELLI_LAYOUT)) {
            return Optional.of(new BaseTableGraph(dataTables.get(0)));
        }

        // Build table graph: linked the metatable and datatables depending of the reading directional preferences
        // in perception of visual stimuli depending of the cultures and writing systems.

        final var readingDirection = this.document.getReadingDirection();
        final var root = BaseTableGraphBuilder.build(metaTables, dataTables, readingDirection);

        if (!this.notifyStepCompleted(new TableGraphBuiltEvent(this, root))) {
            return Optional.empty();
        }

        return Optional.of(root);
    }

    @Override
    public Optional<Table> getTable() {

        final var root = this.getTableGraph();
        if (root.isEmpty()) {
            return Optional.empty();
        }

        final DataTable table;
        if (document.getHints().contains(Document.Hint.INTELLI_LAYOUT)) {
            table = new IntelliTable(this, (BaseTableGraph) root.get(), this.autoHeaderNameEnabled);
        } else {
            table = (DataTable) root.get().getTable();
        }

        // Tag headers

        table.updateHeaderTags();
        this.notifyStepCompleted(new TableReadyEvent(this, table));

        return Optional.of(table);
    }

    public boolean isPivotEnabled() {
        return this.pivotEnabled;
    }

    public SheetStore getSheetStore() {
        return this.sheetStore;
    }

    public int getLastColumnNum(final int rowIndex) {
        final var translatedRow = this.translateRow(rowIndex);
        if (translatedRow < 0) {
            return -1;
        }
        return this.sheetStore.getLastColumnNum(translatedRow) - (this.storeLastColumnNum - this.columnMask.size() + 1);
    }

    public boolean hasCellDataAt(final int colIndex, final int rowIndex) {
        final var translatedColumn = this.translateColumn(colIndex);
        if (translatedColumn < 0) {
            return false;
        }
        final var translatedRow = this.translateRow(rowIndex);
        if (translatedRow < 0) {
            return false;
        }
        return this.sheetStore.hasCellDataAt(translatedColumn, translatedRow);
    }

    public String getCellDataAt(final int colIndex, final int rowIndex) {
        final var translatedColumn = this.translateColumn(colIndex);
        if (translatedColumn < 0) {
            return null;
        }
        final var translatedRow = this.translateRow(rowIndex);
        if (translatedRow < 0) {
            return null;
        }
        return this.sheetStore.getCellDataAt(translatedColumn, translatedRow);
    }

    public int getNumberOfMergedCellsAt(final int colIndex, final int rowIndex) {
        if (this.unmergedAll) {
            return 1;
        }
        final var translatedColumn = this.translateColumn(colIndex);
        if (translatedColumn < 0) {
            return 1;
        }
        final var translatedRow = this.translateRow(rowIndex);
        if (translatedRow < 0) {
            return 1;
        }
        return this.sheetStore.getNumberOfMergedCellsAt(translatedColumn, translatedRow);
    }

    public void patchCell(final int colIndex1, final int rowIndex1, final int colIndex2, final int rowIndex2,
            final String value) {
        final var translatedColumn1 = this.translateColumn(colIndex1);
        if (translatedColumn1 < 0) {
            return;
        }
        final var translatedRow1 = this.translateRow(rowIndex1);
        if (translatedRow1 < 0) {
            return;
        }
        final var translatedColumn2 = this.translateColumn(colIndex2);
        if (translatedColumn2 < 0) {
            return;
        }
        final var translatedRow2 = this.translateRow(rowIndex2);
        if (translatedRow2 < 0) {
            return;
        }
        this.sheetStore.patchCell(translatedColumn1, translatedRow1, translatedColumn2, translatedRow2, value, this.unmergedAll);
    }

    public boolean notifyStepCompleted(final SheetEvent e) {
        for (final var listener : listeners) {
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

    public float getCapillarityThreshold() {
        return this.capillarityThreshold;
    }

    public void setCapillarityThreshold(final float threshold) {
        this.capillarityThreshold = threshold;
    }

    public List<String> getPivotEntityList() {
        if (!this.pivotEnabled) {
            return Collections.emptyList();
        }
        if (this.pivotEntityList == null) {
            return this.getDocument().getModel().getPivotEntityList();
        } else {
            return this.pivotEntityList;
        }
    }

    public void setPivotEntityList(final List<String> pivotEntityList) {
        this.pivotEntityList = pivotEntityList;
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

    public void disablePivot() {
        this.pivotEnabled = false;
    }

    public void disableAutoHeaderName() {
        this.autoHeaderNameEnabled = false;
    }

    public void disableAutoCrop() {
        this.autoCropEnabled = false;
    }

    public void swapRows(int rowIndex1, int rowIndex2) {
        final var tmp = this.rowMask.get(rowIndex1);
        this.rowMask.set(rowIndex1, this.rowMask.get(rowIndex2));
        this.rowMask.set(rowIndex2, tmp);
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
        var result = this.sheetStore.getLastColumnNum(0);
        for (var i = 1; i <= this.sheetStore.getLastRowNum(); i++) {
            result = Math.max(result, this.sheetStore.getLastColumnNum(i));
        }
        return result;
    }

    private final BaseDocument document;
    private final String name;
    private final PatcheableSheetStore sheetStore;
    private final ArrayList<SheetListener> listeners = new ArrayList<SheetListener>();
    private final List<Integer> rowMask;
    private final List<Integer> columnMask;
    private final int storeLastColumnNum;

    private boolean unmergedAll = false;
    private float capillarityThreshold = Settings.DEFAULT_CAPILLARITY_THRESHOLD;
    private PivotOption pivotOption;
    private String pivotKeyFormat;
    private String pivotValueFormat;
    private String pivotTypeFormat;
    private String groupValueFormat;
    private List<String> pivotEntityList;
    private boolean pivotEnabled;
    private boolean autoCropEnabled;
    private boolean autoHeaderNameEnabled;
}
