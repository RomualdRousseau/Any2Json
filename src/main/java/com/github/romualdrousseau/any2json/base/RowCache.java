package com.github.romualdrousseau.any2json.base;

import java.util.Map;
import java.util.LinkedHashMap;

import com.github.romualdrousseau.any2json.config.Settings;

public class RowCache extends LinkedHashMap<Integer, BaseRow> {

    public RowCache() {
        super(Settings.MAX_STORE_ROWS, 0.75F, true);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, BaseRow> eldest) {
        return this.size() > Settings.MAX_STORE_ROWS;
    }
}
