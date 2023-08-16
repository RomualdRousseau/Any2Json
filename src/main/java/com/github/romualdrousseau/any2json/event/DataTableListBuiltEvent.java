package com.github.romualdrousseau.any2json.event;

import java.util.List;

import com.github.romualdrousseau.any2json.Sheet;
import com.github.romualdrousseau.any2json.SheetEvent;
import com.github.romualdrousseau.any2json.base.DataTable;

public class DataTableListBuiltEvent extends SheetEvent {

    public DataTableListBuiltEvent(final Sheet source, final List<DataTable> tables) {
        super(source);
        this.dataTables = tables;
    }

    public List<DataTable> getDataTables() {
        return this.dataTables;
    }

    private final List<DataTable> dataTables;
}
