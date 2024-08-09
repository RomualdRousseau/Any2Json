package com.github.romualdrousseau.any2json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.romualdrousseau.any2json.classifier.SimpleTagClassifier;

/**
 * Unit test for Any2Json.
 */
public class Test_Any2Json {

    private static final Model model;
    static {
        model = ModelDB.createConnection("sales-english");
    }

    @Test
    public void testSimpleClassifierWithEnclosedTag() throws Exception {
        try (final var classifer = new SimpleTagClassifier(model, TagClassifier.TagStyle.NONE)) {
            assertEquals("customerName", classifer.predict("customer name ($customerName)", null, null));
        }
    }

    @Test
    public void testSimpleClassifierWithSnake() throws Exception {
        try (final var classifer = new SimpleTagClassifier(model, TagClassifier.TagStyle.SNAKE)) {
            assertEquals("customer_name_u", classifer.predict("customer name_u", null, null));
            assertEquals("customer_name_u", classifer.predict("customer_name_u", null, null));
            assertEquals("customer_name_u", classifer.predict("customerName_u", null, null));
            assertEquals("customer_name_u", classifer.predict("customername_u", null, null));
        }
    }

    @Test
    public void testSimpleClassifierWithCamel() throws Exception {
        try (final var classifer = new SimpleTagClassifier(model, TagClassifier.TagStyle.CAMEL)) {
            assertEquals("customerNameu", classifer.predict("customer name_u", null, null));
            assertEquals("customerNameu", classifer.predict("customer_name_u", null, null));
            assertEquals("customerNameu", classifer.predict("customerName_u", null, null));
            assertEquals("customerNameu", classifer.predict("customername_u", null, null));
        }
    }

    @Test
    public void testSimpleClassifierCompatible() throws Exception {
        try (final var classifer = new SimpleTagClassifier(model, TagClassifier.TagStyle.NONE)) {
            assertEquals("customer_name", classifer.predict("customer name", null, null));
            assertEquals("customer_name", classifer.predict("customer_name", null, null));
            assertEquals("customerName", classifer.predict("customername", null, null));
        }
    }
}
