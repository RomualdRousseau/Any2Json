package com.github.romualdrousseau.any2json;

import java.io.IOException;
import java.net.URISyntaxException;

import com.github.romualdrousseau.any2json.modeldata.JsonModelBuilder;

public class ModelDB {

    public static Model createConnection(final String modelName) {
        try {
            return new JsonModelBuilder()
                    .fromResource(new ModelDB().getClass(), String.format("/data/%s.json", modelName))
                    .build();
        } catch (final URISyntaxException | IOException x) {
            assert false : x.getMessage();
            return null;
        }
    }
}
