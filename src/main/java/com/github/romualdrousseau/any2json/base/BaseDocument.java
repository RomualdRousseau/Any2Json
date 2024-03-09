package com.github.romualdrousseau.any2json.base;

import java.util.EnumSet;

import com.github.romualdrousseau.any2json.Document;
import com.github.romualdrousseau.any2json.TableParser;
import com.github.romualdrousseau.any2json.TagClassifier;
import com.github.romualdrousseau.any2json.Model;
import com.github.romualdrousseau.any2json.ReadingDirection;
import com.github.romualdrousseau.any2json.Sheet;
import com.github.romualdrousseau.any2json.SheetParser;
import com.github.romualdrousseau.any2json.classifier.SimpleTagClassifier;
import com.github.romualdrousseau.any2json.config.DynamicPackages;
import com.github.romualdrousseau.any2json.parser.sheet.SheetBitmapParser;
import com.github.romualdrousseau.any2json.parser.sheet.SimpleSheetParser;
import com.github.romualdrousseau.any2json.parser.table.SimpleTableParser;
import com.github.romualdrousseau.any2json.readdir.GutenbergDiagonal;
import com.github.romualdrousseau.any2json.transform.op.StitchRows;

public abstract class BaseDocument implements Document {

    public BaseDocument() {
        this.updateParsersAndClassifiers();
        // GutenbergDiagonal (or LRTB) is the default reading direction.
        this.readingDirection = new GutenbergDiagonal();
    }

    @Override
    public void close() {
        try {
            if (this.tableParser != null) {
                this.tableParser.close();
                this.tableParser = null;
            }
            if (this.tagClassifier != null) {
                this.tagClassifier.close();
                this.tagClassifier = null;
            }
        } catch (final Exception x) {
            throw new RuntimeException(x);
        }
    }

    @Override
    public SheetParser getSheetParser() {
        return this.sheetParser;
    }

    @Override
    public TableParser getTableParser() {
        return this.tableParser;
    }

    @Override
    public TagClassifier getTagClassifier() {
        return this.tagClassifier;
    }

    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public Document setModel(final Model model) {
        this.model = model;
        return this;
    }

    @Override
    public EnumSet<Hint> getHints() {
        return this.hints;
    }

    @Override
    public Document setHints(final EnumSet<Hint> hints) {
        this.hints = hints;
        this.updateParsersAndClassifiers();
        return this;
    }

    @Override
    public String getRecipe() {
        return this.recipe;
    }

    @Override
    public Document setRecipe(final String recipe) {
        this.recipe = recipe;
        return this;
    }

    @Override
    public Document setRecipe(final String... recipe) {
        this.recipe = String.join("\n", recipe);
        return this;
    }

    @Override
    public ReadingDirection getReadingDirection() {
        return this.readingDirection;
    }

    @Override
    public BaseDocument setReadingDirection(final ReadingDirection readingDirection) {
        this.readingDirection = readingDirection;
        return this;
    }

    @Override
    public Iterable<Sheet> sheets() {
        return new SheetIterable(this);
    }

    public BaseDocument setSheetParser(final SheetParser sheetParser) {
        this.sheetParser = sheetParser;
        return this;
    }

    public BaseDocument setTableParser(final TableParser tableParser) {
        this.tableParser = tableParser;
        return this;
    }

    public BaseDocument setTagClassifier(final TagClassifier tagClassifier) {
        this.tagClassifier = tagClassifier;
        return this;
    }

    public void autoRecipe(final BaseSheet sheet) {
        if (this.hints.contains(Document.Hint.INTELLI_LAYOUT)) {
            StitchRows.Apply(sheet);
        }
    }

    public void updateParsersAndClassifiers() {
        if (this.hints.contains(Document.Hint.INTELLI_LAYOUT)) {
            this.sheetParser = new SheetBitmapParser();
            this.tableParser = DynamicPackages.GetElementParserFactory()
                    .map(x -> x.newInstance(this.model))
                    .orElseGet(SimpleTableParser::new);
        } else {
            this.sheetParser = new SimpleSheetParser();
            this.tableParser = new SimpleTableParser();
        }
        if (this.hints.contains(Document.Hint.INTELLI_TAG)) {
            this.tagClassifier = DynamicPackages.GetTagClassifierFactory()
                    .map(x -> x.newInstance(this.model))
                    .orElseGet(() -> new SimpleTagClassifier(this.model));
        } else {
            this.tagClassifier = new SimpleTagClassifier(this.model);
        }
    }

    private Model model = Model.Default;
    private EnumSet<Hint> hints = EnumSet.noneOf(Document.Hint.class);
    private String recipe = "";
    private SheetParser sheetParser;
    private TableParser tableParser;
    private TagClassifier tagClassifier;
    private ReadingDirection readingDirection;
}
