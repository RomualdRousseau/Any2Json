package com.github.romualdrousseau.any2json;

import java.util.List;

import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.any2json.base.BaseTable;

public interface SheetParser {

    List<BaseTable> findAllElements(final BaseSheet sheet);
}
