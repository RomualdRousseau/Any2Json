package com.github.romualdrousseau.any2json;

import java.io.File;
import java.util.EnumSet;

public interface Document extends AutoCloseable {

    enum Hint {
        INTELLI_EXTRACT,
        INTELLI_LAYOUT,
        INTELLI_TAG
    }

    SheetParser getSheetParser();

    TableParser getTableParser();

    TagClassifier getTagClassifier();

    boolean open(final File file, final String encoding, final String password);

    void close();

    Model getModel();

    Document setModel(final Model model);

    EnumSet<Hint> getHints();

    Document setHints(final EnumSet<Hint> hints);

    String getRecipe();

    Document setRecipe(final String recipe);

    Document setRecipe(final String... recipe);

    ReadingDirection getReadingDirection();

    Document setReadingDirection(final ReadingDirection readingDirection);

    int getNumberOfSheets();

    String getSheetNameAt(final int i);

    Sheet getSheetAt(final int i);

    Iterable<Sheet> sheets();
}
