package com.github.romualdrousseau.any2json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class ModelBuilder {

    public ModelBuilder() {
        this.reset();
    }

    public ModelBuilder reset() {
        this.jsonModel = JSON.newObject();
        this.entities = Collections.emptyList();
        this.patterns = Collections.emptyMap();
        this.filters = Collections.emptyList();
        this.pivotEntities = Collections.emptyList();
        this.tags = Collections.emptyList();
        this.requiredTags = Collections.emptyList();
        this.tableParser = null;
        this.tagClassifier = null;
        this._updateJSON();
        return this;
    }

    public ModelBuilder fromPath(final Path path) {
        return this.fromJSON(JSON.loadObject(path));
    }

    public ModelBuilder fromURI(final String uri) throws IOException, InterruptedException {
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Error loading model");
        }
        return this.fromJSON(JSON.objectOf(response.body()));
    }

    public ModelBuilder fromJSON(final JSONObject model) {
        this.jsonModel = model;
        this.entities = JSON.<String>streamOf(this.jsonModel.getArray("entities")).collect(Collectors.toList());
        this.patterns = JSON.<JSONObject>streamOf(this.jsonModel.getArray("patterns"))
                .collect(Collectors.toMap(x -> x.getString("key"), x -> x.getString("value")));
        this.filters = JSON.<String>streamOf(this.jsonModel.getArray("filters")).collect(Collectors.toList());
        this.pivotEntities = JSON.<String>streamOf(this.jsonModel.getArray("pivotEntityList")).collect(Collectors.toList());
        this.tags = JSON.<String>streamOf(this.jsonModel.getArray("tags")).collect(Collectors.toList());
        this.requiredTags = JSON.<String>streamOf(this.jsonModel.getArray("requiredTags")).collect(Collectors.toList());
        return this;
    }

    public List<String> getEntityList() {
        return this.entities;
    }

    public ModelBuilder setEntityList(final List<String> entities) {
        this.entities = entities;
        return this;
    }

    public Map<String, String> getPatternMap() {
        return this.patterns;
    }

    public ModelBuilder setPatternMap(final Map<String, String> patterns) {
        this.patterns = patterns;
        return this;
    }

    public List<String> getFilters() {
        return this.filters;
    }

    public ModelBuilder setFilters(final List<String> filters) {
        this.filters = filters;
        return this;
    }

    public List<String> getPivotEntityList() {
        return this.pivotEntities;
    }

    public ModelBuilder setPivotEntityList(final List<String> pivotEntities) {
        this.pivotEntities = pivotEntities;
        return this;
    }

    public List<String> getTagList() {
        return this.tags;
    }

    public ModelBuilder setTagList(final List<String> tags) {
        this.tags = tags;
        return this;
    }

    public List<String> getRequiredTagList() {
        return this.requiredTags;
    }

    public ModelBuilder setRequiredTagList(final List<String> requiredTags) {
        this.requiredTags = requiredTags;
        return this;
    }

    public ModelBuilder setTableParser(final TableParser tableParser) {
        this.tableParser = tableParser;
        return this;
    }

    public ModelBuilder setTagClassifier(final TagClassifier tagClassifier) {
        this.tagClassifier = tagClassifier;
        return this;
    }

    public Model build() {
        this._updateJSON();
        final var model = new Model(this.jsonModel);
        if (this.tableParser != null) {
            this.tableParser.setModel(model);
        }
        if (this.tagClassifier != null) {
            this.tagClassifier.setModel(model);
        }
        return model;
    }

    private void _updateJSON() {
        this.jsonModel.setArray("entities", JSON.arrayOf(this.entities));
        this.jsonModel.setArray("patterns", JSON.arrayOf(this.patterns));
        this.jsonModel.setArray("filters", JSON.arrayOf(this.filters));
        this.jsonModel.setArray("pivotEntityList", JSON.arrayOf(this.pivotEntities));
        this.jsonModel.setArray("tags", JSON.arrayOf(this.tags));
        this.jsonModel.setArray("requiredTags", JSON.arrayOf(this.requiredTags));
    }

    private JSONObject jsonModel;
    private List<String> entities;
    private Map<String, String> patterns;
    private List<String> filters;
    private List<String> pivotEntities;
    private List<String> tags;
    private List<String> requiredTags;
    private TableParser tableParser;
    private TagClassifier tagClassifier;
}
