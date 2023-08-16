package com.github.romualdrousseau.any2json.event;

import java.util.List;

import com.github.romualdrousseau.any2json.Sheet;
import com.github.romualdrousseau.any2json.SheetEvent;
import com.github.romualdrousseau.any2json.base.MetaTable;

public class MetaTableListBuiltEvent extends SheetEvent {

    public MetaTableListBuiltEvent(final Sheet source, final List<MetaTable> tables) {
        super(source);
        this.MetaTables = tables;
    }

    public List<MetaTable> getMetaTables() {
        return this.MetaTables;
    }

    private final List<MetaTable> MetaTables;
}
