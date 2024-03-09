package com.github.romualdrousseau.any2json.classifier;

import java.util.List;
import java.util.regex.Pattern;

import com.github.romualdrousseau.any2json.Model;
import com.github.romualdrousseau.any2json.TagClassifier;
import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.preprocessing.Text;
import com.github.romualdrousseau.shuju.preprocessing.tokenizer.ShingleTokenizer;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class SimpleTagClassifier implements TagClassifier {

    private final Pattern pattern = Pattern.compile(" \\(\\$(.*)\\)$");

    public SimpleTagClassifier(final Model model) {
        final List<String> lexicon;
        if (model != null && model.toJSON().getArray("lexicon") != null) {
            lexicon = JSON.<String>streamOf(model.toJSON().getArray("lexicon")).toList();
        } else{
            lexicon = StringUtils.getSymbols().stream().toList();
        }

        this.tagTokenizer = new ShingleTokenizer(lexicon, 1);
        this.snakeMode = false;
        this.camelMode = false;
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void updateModel(final Model model) {
    }

    @Override
    public String predict(String name, List<String> entities, List<String> context) {
        final var m = pattern.matcher(name);
        if (m.find()) {
            return m.group(1);
        } else {
            return this.ensureTagStyle(name);
        }
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
        if (text.indexOf(" ") > 0 || text.indexOf("_") > 0) {
            return StringUtils.toSnake(text, this.tagTokenizer);
        } else {
            return StringUtils.toCamel(text, this.tagTokenizer);
        }
    }

    private final Text.ITokenizer tagTokenizer;
    private boolean snakeMode;
    private boolean camelMode;
}
