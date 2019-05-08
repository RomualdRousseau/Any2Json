package com.github.romualdrousseau.any2json.classifiers;

import com.github.romualdrousseau.any2json.ITagClassifier;
import com.github.romualdrousseau.shuju.DataRow;
import com.github.romualdrousseau.shuju.DataSet;
import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.LayerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.Loss;
import com.github.romualdrousseau.shuju.ml.nn.Model;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.activation.LeakyRelu;
import com.github.romualdrousseau.shuju.ml.nn.activation.Softmax;
import com.github.romualdrousseau.shuju.ml.nn.loss.SoftmaxCrossEntropy;
import com.github.romualdrousseau.shuju.ml.nn.normalizer.BatchNormalizer;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.builder.OptimizerAdamBuilder;
import com.github.romualdrousseau.shuju.nlp.NgramList;
import com.github.romualdrousseau.shuju.nlp.RegexList;
import com.github.romualdrousseau.shuju.nlp.StopWordList;
import com.github.romualdrousseau.shuju.nlp.StringList;

public class NGramNNClassifier implements ITagClassifier {
    private NgramList ngrams;
    private RegexList entities;
    private StopWordList stopwords;
    private StringList tags;
    private Model model;
    private Optimizer optimizer;
    private Loss criterion;
    private float accuracy;
    private float mean;

    public NGramNNClassifier(NgramList ngrams, RegexList entities, StopWordList stopwords, StringList tags) {
        this.accuracy = 0.0f;
        this.mean = 1.0f;
        this.ngrams = ngrams;
        this.entities = entities;
        this.stopwords = stopwords;
        this.tags = tags;
        this.buildModel();
    }

    public NGramNNClassifier(JSONObject json) {
        this(new NgramList(json.getJSONObject("ngrams")), new RegexList(json.getJSONObject("entities")),
                new StopWordList(json.getJSONArray("stopwords")), new StringList(json.getJSONObject("tags")));
        this.model.fromJSON(json.getJSONArray("model"));
    }

    public int getSampleCount() {
        return 30;
    }

    public StopWordList getStopWordList() {
        return this.stopwords;
    }

    public RegexList getEntityList() {
        return this.entities;
    }

    public NgramList getWordList() {
        return this.ngrams;
    }

    public StringList getTagList() {
        return this.tags;
    }

    public Model getModel() {
        return this.model;
    }

    public float getMean() {
        return this.mean;
    }

    public float getAccuracy() {
        return this.accuracy;
    }

    public void fit(DataSet dataset) {
        if (dataset.rows().size() == 0 || this.mean < 1e-4) {
            return;
        }

        final int nCount = 10;
        final int total = dataset.shuffle().rows().size();
        final int slice = total / nCount;

        this.accuracy = 0.0f;
        this.mean = 0.0f;

        for (int n = 0; n < nCount; n++) {
            int d1 = total - slice * (n + 1);
            int d2 = total - slice * n;
            DataSet trainingSet = dataset.subset(0, d1).join(dataset.subset(d2, total));
            DataSet testSet = dataset.subset(d1, d2);
            //System.out.println(String.format("0 %d %d %d", d1, d2, total));

            float sumAccu = 0.0f;
            float sumMean = 0.0f;

            this.optimizer.zeroGradients();

            for (DataRow data : trainingSet.rows()) {
                Vector input = data.featuresAsOneVector();
                Vector target = data.label();

                Layer output = this.model.model(input);
                Loss loss = this.criterion.loss(output, target);

                if (output.detach().argmax(0) != target.argmax()) {
                    loss.backward();
                }
            }

            this.optimizer.step();

            for (DataRow data : testSet.rows()) {
                Vector input = data.featuresAsOneVector();
                Vector target = data.label();

                Layer output = this.model.model(input);
                Loss loss = this.criterion.loss(output, target);

                if (output.detach().argmax(0) == target.argmax()) {
                    sumAccu++;
                }

                sumMean += loss.getValue().flatten(0);

                if (Float.isNaN(sumMean)) {
                    sumMean = (float) slice;
                }
            }

            this.accuracy += Scalar.constrain(sumAccu / (float) slice, 0, 1);
            this.mean += Scalar.constrain(sumMean / (float) slice, 0, 1);
        }

        this.accuracy /= (float) nCount;
        this.mean /= (float) nCount;
    }

    public String predict(DataRow row) {
        Vector input = row.featuresAsOneVector();
        Vector output = this.model.model(input).detachAsVector();

        int tagIndex = output.argmax();
        if (tagIndex >= this.tags.size()) {
            tagIndex = 0;
        }
        return this.tags.get(tagIndex);
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        json.setJSONObject("ngrams", this.ngrams.toJSON());
        json.setJSONObject("entities", this.entities.toJSON());
        json.setJSONArray("stopwords", this.stopwords.toJSON());
        json.setJSONObject("tags", this.tags.toJSON());
        json.setJSONArray("model", this.model.toJSON());
        return json;
    }

    private void buildModel() {
        final int inputCount = this.entities.getVectorSize() + 2 * this.ngrams.getVectorSize();
        final int hiddenCount = inputCount / 2;
        final int outputCount = this.tags.getVectorSize();

        final Layer layer1 = new LayerBuilder().setInputUnits(inputCount).setUnits(hiddenCount)
                .setActivation(new LeakyRelu()).setNormalizer(new BatchNormalizer()).build();

        final Layer layer2 = new LayerBuilder().setInputUnits(hiddenCount).setUnits(outputCount)
                .setActivation(new Softmax()).build();

        this.model = new Model().add(layer1).add(layer2);

        this.optimizer = new OptimizerAdamBuilder().build(this.model);

        this.criterion = new Loss(new SoftmaxCrossEntropy());
    }
}
