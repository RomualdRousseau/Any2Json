package com.github.romualdrousseau.any2json;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.net.URL;
import java.net.URISyntaxException;

import org.junit.Test;

import com.github.romualdrousseau.shuju.json.JSONObject;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void testReadVariousDocuments() {
        JSONObject model = ModelDB.createConnection("sales-english");
        System.out.println(model.getArray("vocabulary"));
    }

    private Document loadDocument(String resourceName, String encoding) {
        return DocumentFactory.createInstance(this.getResourcePath(resourceName).toFile(), encoding);
    }

    private Path getResourcePath(String resourceName) {
        try {
            URL resourceUrl = this.getClass().getResource(resourceName);
            assert resourceUrl != null;
            return Paths.get(resourceUrl.toURI());
        } catch (URISyntaxException x) {
            assert false : x.getMessage();
            return null;
        }
    }
}
