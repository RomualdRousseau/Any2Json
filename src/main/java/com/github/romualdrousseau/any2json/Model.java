package com.github.romualdrousseau.any2json;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.preprocessing.Text;
import com.github.romualdrousseau.shuju.preprocessing.comparer.RegexComparer;
import com.github.romualdrousseau.shuju.types.Tensor;

public class Model {

    public static final Model Default = new Model(Collections.emptyList(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

    public Model(final List<String> entities, final Map<String, String> patterns, final List<String> filters,
            final List<String> pivotEntityList, final List<String> tags, final List<String> requiredTags) {
        this.model = JSON.newObject();
        this.model.setArray("entities", JSON.arrayOf(entities));
        this.model.setArray("patterns", JSON.arrayOf(patterns));
        this.model.setArray("filters", JSON.arrayOf(filters));
        this.model.setArray("pivotEntityList", JSON.arrayOf(pivotEntityList));
        this.model.setArray("tags", JSON.arrayOf(tags));
        this.model.setArray("requiredTags", JSON.arrayOf(requiredTags));
        this.entities = entities;
        this.patterns = patterns;
        this.filters = filters;
        this.pivotEntityList = pivotEntityList;
        this.tags = tags;
        this.requiredTags = requiredTags;
        this.comparer = new RegexComparer(this.patterns);
    }

    public Model(final JSONObject model) {
        this.model = model;
        this.entities = JSON.<String>streamOf(model.getArray("entities")).toList();
        this.patterns = JSON.<JSONObject>streamOf(model.getArray("patterns"))
                .collect(Collectors.toMap(x -> x.getString("key"), x -> x.getString("value")));
        this.filters = JSON.<String>streamOf(model.getArray("filters")).toList();
        this.pivotEntityList = JSON.<String>streamOf(model.getArray("pivotEntityList")).toList();
        this.tags = JSON.<String>streamOf(model.getArray("tags")).toList();
        this.requiredTags = JSON.<String>streamOf(model.getArray("requiredTags")).toList();
        this.comparer = new RegexComparer(this.patterns);
    }

    public List<String> getFilters() {
        return this.filters;
    }

    public List<String> getEntityList() {
        return this.entities;
    }

    public List<String> getPivotEntityList() {
        return this.pivotEntityList;
    }

    public List<String> getTagList() {
        return this.tags;
    }

    public List<String> getRequiredTagList() {
        return this.requiredTags;
    }

    public String toEntityName(final String value) {
        return this.comparer.anonymize(value);
    }

    public Optional<String> toEntityValue(final String value) {
        return this.comparer.find(value);
    }

    public Tensor toEntityVector(final String value) {
        return Tensor.of(Text.to_categorical(value, this.entities, this.comparer).stream()
                .mapToDouble(x -> (double) x).toArray());
    }

    public JSONObject toJSON() {
        return model;
    }

    private final JSONObject model;
    private final List<String> entities;
    private final Map<String, String> patterns;
    private final List<String> filters;
    private final List<String> pivotEntityList;
    private final List<String> tags;
    private final List<String> requiredTags;
    private final RegexComparer comparer;
}
