package com.github.romualdrousseau.any2json.classifiers;

import com.github.romualdrousseau.any2json.ClassifierFactory;
import com.github.romualdrousseau.any2json.IClassifierBuilder;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.nlp.NgramList;
import com.github.romualdrousseau.shuju.nlp.RegexList;
import com.github.romualdrousseau.shuju.nlp.StopWordList;
import com.github.romualdrousseau.shuju.nlp.StringList;

public class LayexAndNetClassifierBuilder implements IClassifierBuilder {

    public LayexAndNetClassifierBuilder setModel(JSONObject model) {
        this.model = model;
        return this;
    }

    public LayexAndNetClassifierBuilder setNgramList(NgramList ngramList) {
        this.ngramList = ngramList;
		return this;
    }

    public LayexAndNetClassifierBuilder setEntityList(RegexList entityList) {
        this.entityList = entityList;
		return this;
    }

    public LayexAndNetClassifierBuilder setStopWordList(StopWordList stopWordList) {
        this.stopWordList = stopWordList;
		return this;
    }

    public LayexAndNetClassifierBuilder setTagList(StringList tagList) {
        this.tagList = tagList;
		return this;
    }

    public LayexAndNetClassifierBuilder setRequiredTagList(String[] requiredTagList) {
        this.requiredTagList = requiredTagList;
		return this;
    }

    public LayexAndNetClassifierBuilder setPivotEntityList(String[] pivotEntityList) {
        this.pivotEntityList = pivotEntityList;
		return this;
	}

    public LayexAndNetClassifierBuilder setMetaLayexes(String[] metaLayexes) {
        this.metaLayexes = metaLayexes;
		return this;
    }

    public LayexAndNetClassifierBuilder setDataLayexes(String[] dataLayexes) {
        this.dataLayexes = dataLayexes;
		return this;
	}

    public ClassifierFactory build() {
        final LayexAndNetClassifier classifier;
        if (model == null) {
            if (metaLayexes != null && dataLayexes != null) {
                classifier = new LayexAndNetClassifier(ngramList, entityList, stopWordList, tagList, requiredTagList, pivotEntityList, metaLayexes, dataLayexes);
            } else {
                classifier = new LayexAndNetClassifier(ngramList, entityList, stopWordList, tagList, requiredTagList, pivotEntityList);
            }
        } else {
            classifier = new LayexAndNetClassifier(model);
        }
        return new ClassifierFactory()
            .setLayoutClassifier(classifier)
            .setTagClassifier(classifier);
    }

    private JSONObject model;
    private NgramList ngramList;
    private RegexList entityList;
    private StopWordList stopWordList;
    private StringList tagList;
    private String[] requiredTagList;
    private String[] pivotEntityList;
    private String[] metaLayexes;
    private String[] dataLayexes;
}
