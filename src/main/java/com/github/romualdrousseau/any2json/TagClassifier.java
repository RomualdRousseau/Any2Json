package com.github.romualdrousseau.any2json;

import java.util.List;

public interface TagClassifier extends AutoCloseable {

    String predict(String name, List<String> entities, List<String> context);
}
