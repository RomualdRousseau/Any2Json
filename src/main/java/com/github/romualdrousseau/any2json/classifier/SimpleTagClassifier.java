package com.github.romualdrousseau.any2json.classifier;

import java.util.List;

import com.github.romualdrousseau.any2json.TagClassifier;

public class SimpleTagClassifier implements TagClassifier {

    @Override
    public void close() throws Exception {
    }

    @Override
    public String predict(String name, List<String> entities, List<String> context) {
        return this.ensureTagStyle(name.replaceAll(" \\(\\$.*\\)", ""));
    }

    private String ensureTagStyle(final String text) {
        if (text.indexOf(" ") > 0) {
            return text
                    .replaceAll("%+", "percent")
                    .replaceAll("\\$+", "dollar")
                    .replaceAll("\\W+", " ")
                    .trim()
                    .replaceAll("\\s+", "_")
                    .replaceAll("_+", "_")
                    .toLowerCase();
        } else {
            return text
                    .replaceAll("%+", "percent")
                    .replaceAll("\\$+", "dollar")
                    .replaceAll("\\W+", "")
                    .trim()
                    .toLowerCase();
        }
    }
}
