package com.github.romualdrousseau.any2json.event;

import java.util.List;

import com.github.romualdrousseau.any2json.Sheet;
import com.github.romualdrousseau.any2json.SheetEvent;
import com.github.romualdrousseau.any2json.base.BaseTable;

public class AllTablesExtractedEvent extends SheetEvent {

    public AllTablesExtractedEvent(final Sheet source, final List<BaseTable> tables) {
        super(source);
        this.tables = tables;
    }

    public List<BaseTable> getTables() {
        return this.tables;
    }

    private final List<BaseTable> tables;
}
