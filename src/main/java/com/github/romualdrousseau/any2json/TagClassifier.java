package com.github.romualdrousseau.any2json;

import java.util.List;

public interface TagClassifier extends AutoCloseable {

    enum TagStyle {
        NONE,
        CAMEL,
        SNAKE
    }

    Model getModel();

    TagClassifier setModel(final Model model);

    String predict(final String name, final List<String> entities, final List<String> context);

    TagClassifier.TagStyle getTagStyle();

    TagClassifier setTagStyle(final TagClassifier.TagStyle mode);

    String ensureTagStyle(final String text);
}
