package com.github.romualdrousseau.any2json.classifier;

import java.util.List;

import com.github.romualdrousseau.any2json.Model;
import com.github.romualdrousseau.any2json.TagClassifier;
import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.preprocessing.Text;
import com.github.romualdrousseau.shuju.preprocessing.tokenizer.ShingleTokenizer;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class SimpleTagClassifier implements TagClassifier {

    public SimpleTagClassifier(final Model model) {
        final List<String> vocabulary;
        if (model != null && model.toJSON().getArray("vocabulary") != null) {
            vocabulary = JSON.<String>streamOf(model.toJSON().getArray("vocabulary")).toList();
        } else{
            vocabulary = StringUtils.getSymbols();
        }

        this.tagTokenizer = new ShingleTokenizer(vocabulary);
        this.snakeMode = false;
        this.camelMode = false;
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public String predict(String name, List<String> entities, List<String> context) {
        return this.ensureTagStyle(name.replaceAll(" \\(\\$.*\\)", ""));
    }

    @Override
    public SimpleTagClassifier setSnakeMode(final boolean snakeMode) {
        this.snakeMode = snakeMode;
        return this;
    }

    @Override
    public SimpleTagClassifier setCamelMode(final boolean camelMode) {
        this.camelMode = camelMode;
        return this;
    }

    @Override
    public String ensureTagStyle(final String text) {
        if (this.snakeMode) {
            return StringUtils.toSnake(text, this.tagTokenizer);
        }
        if (this.camelMode) {
            return StringUtils.toCamel(text, this.tagTokenizer);
        }
        if (text.indexOf(" ") > 0) {
            return StringUtils.toSnake(text, this.tagTokenizer);
        } else {
            return StringUtils.toCamel(text, this.tagTokenizer);
        }
    }

    private final Text.ITokenizer tagTokenizer;
    private boolean snakeMode;
    private boolean camelMode;
}
