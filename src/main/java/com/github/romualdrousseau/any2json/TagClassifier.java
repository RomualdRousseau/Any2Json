package com.github.romualdrousseau.any2json;

import java.util.List;

public interface TagClassifier extends AutoCloseable {

    void updateModel(final Model model);

    String predict(final String name, final List<String> entities, final List<String> context);

    TagClassifier setSnakeMode(final boolean snake);

    TagClassifier setCamelMode(final boolean snake);

    String ensureTagStyle(final String text);
}
