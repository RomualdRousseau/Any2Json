package com.github.romualdrousseau.archery.event;

import java.util.List;

import com.github.romualdrousseau.archery.Sheet;
import com.github.romualdrousseau.archery.SheetEvent;
import com.github.romualdrousseau.archery.base.BaseTable;

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
