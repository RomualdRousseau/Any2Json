package com.github.romualdrousseau.any2json.base;

import java.util.EnumSet;

import com.github.romualdrousseau.any2json.Document;
import com.github.romualdrousseau.any2json.TableParser;
import com.github.romualdrousseau.any2json.TagClassifier;
import com.github.romualdrousseau.any2json.Model;
import com.github.romualdrousseau.any2json.Sheet;
import com.github.romualdrousseau.any2json.SheetParser;
import com.github.romualdrousseau.any2json.classifier.SimpleTagClassifier;
import com.github.romualdrousseau.any2json.config.DynamicPackages;
import com.github.romualdrousseau.any2json.parser.sheet.SemiStructuredSheetBitmapParser;
import com.github.romualdrousseau.any2json.parser.sheet.StructuredSheetParser;
import com.github.romualdrousseau.any2json.parser.table.SimpleTableParser;

public abstract class BaseDocument implements Document {

    public BaseDocument() {
        this.updateParsersAndClassifiers();
    }

    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public Document setModel(Model model) {
        this.model =model;
        return this;
    }

    @Override
    public EnumSet<Hint> getHints() {
        return this.hints;
    }

    @Override
    public Document setHints(EnumSet<Hint> hints) {
        this.hints = hints;
        this.updateParsersAndClassifiers();
        return this;
    }

    @Override
    public String getRecipe() {
        return this.recipe;
    }

    @Override
    public Document setRecipe(String recipe) {
        this.recipe = recipe;
        return this;
    }

    @Override
    public Iterable<Sheet> sheets() {
        return new SheetIterable(this);
    }

    public SheetParser getSheetParser() {
        return this.sheetParser;
    }

    public TableParser getElementParser() {
        return this.elementParser;
    }

    public TagClassifier getTagClassifier() {
        return this.tagClassifier;
    }

    protected void updateParsersAndClassifiers() {
        if(this.hints.contains(Document.Hint.NONE)) {
            this.sheetParser = new StructuredSheetParser();
            this.elementParser = new SimpleTableParser();
            this.tagClassifier = new SimpleTagClassifier();
        }
        if(this.hints.contains(Document.Hint.INTELLI_LAYOUT)) {
            this.sheetParser = new SemiStructuredSheetBitmapParser();
            this.elementParser = DynamicPackages.GetElementParserFactory()
                .map(x -> x.newInstance(this.model))
                .orElseGet(SimpleTableParser::new);
        }
        if(this.hints.contains(Document.Hint.INTELLI_TAG)) {
            this.tagClassifier = DynamicPackages.GetTagClassifierFactory()
                .map(x -> x.newInstance(this.model))
                .orElseGet(SimpleTagClassifier::new);
        }
    }

    private Model model = Model.Default;
    private EnumSet<Hint> hints = EnumSet.of(Document.Hint.NONE);
    private String recipe = "";
    private SheetParser sheetParser;
    private TableParser elementParser;
    private TagClassifier tagClassifier;
}
