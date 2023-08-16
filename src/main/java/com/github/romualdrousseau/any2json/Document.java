package com.github.romualdrousseau.any2json;

import java.io.File;
import java.util.EnumSet;

public interface Document extends AutoCloseable {

    enum Hint {
        NONE,
        INTELLI_LAYOUT,
        INTELLI_TAG
    }

    boolean open(File file, String encoding, final String password);

    void close();

    Model getModel();

    Document setModel(Model model);

    EnumSet<Hint> getHints();

    Document setHints(EnumSet<Hint> hints);

    String getRecipe();

    Document setRecipe(String recipe);

    int getNumberOfSheets();

    Sheet getSheetAt(int i);

    Iterable<Sheet> sheets();
}
