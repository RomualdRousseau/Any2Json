package com.github.romualdrousseau.any2json.classifier;

import java.util.List;

import com.github.romualdrousseau.any2json.TagClassifier;

public class SimpleTagClassifier implements TagClassifier{

    @Override
    public void close() throws Exception {
    }

    @Override
    public String predict(String name, List<String> entities, List<String> context) {
        return this.ensureTagStyle(name);
    }

    private String ensureTagStyle(final String text) {
        if (text.indexOf(" ") > 0) {
            return text.replaceAll("\\W+", " ").trim().replaceAll(" ", "_").toLowerCase();
        } else {
            return text.replaceAll("\\W+", "");
        }
    }
}
