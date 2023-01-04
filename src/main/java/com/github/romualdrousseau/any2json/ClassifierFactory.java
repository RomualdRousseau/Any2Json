package com.github.romualdrousseau.any2json;

import java.util.Optional;

public class ClassifierFactory {

    public ClassifierFactory setLayoutClassifier(ILayoutClassifier layoutClassifier) {
        this.layoutClassifier = layoutClassifier;
        return this;
    }

    public <T> ClassifierFactory setTagClassifier(ITagClassifier<T> tagClassifier) {
        this.tagClassifier = tagClassifier;
        return this;
    }

    public Optional<ILayoutClassifier> getLayoutClassifier() {
        return Optional.ofNullable(this.layoutClassifier);
    }

    @SuppressWarnings("unchecked") // In god we trust :)
    public <T> Optional<ITagClassifier<T>> getTagClassifier() {
        return Optional.ofNullable((ITagClassifier<T>) this.tagClassifier);
    }

    private ILayoutClassifier layoutClassifier;
    private ITagClassifier<?> tagClassifier;
}
