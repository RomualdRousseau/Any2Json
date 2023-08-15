package com.github.romualdrousseau.any2json;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UnknownFormatConversionException;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class DocumentFactory {

    public final static int DEFAULT_SAMPLE_COUNT = 200;
    public final static float DEFAULT_RATIO_SIMILARITY = 0.35f;
    public final static String PIVOT_KEY_SUFFIX = "#PIVOT?";
    public final static String PIVOT_VALUE_SUFFIX = "#VALUE?";
    public final static float DEFAULT_ENTITY_PROBABILITY = 0.6f;
    public final static int MAX_STORE_ROWS = 10000;
    public final static String PACKAGE_LOADER_PREFIX = "com.github.romualdrousseau.any2json.loader";

    public static Document createInstance(final String filePath, final String encoding) {
        return DocumentFactory.createInstance(new File(filePath), encoding, null, true);
    }

    public static Document createInstance(final String filePath, final String encoding, final String password) {
        return DocumentFactory.createInstance(new File(filePath), encoding, password, true);
    }

    public static Document createInstance(final String filePath, final String encoding, final String password,
            final boolean wellFormed) {
        return DocumentFactory.createInstance(new File(filePath), encoding, password, wellFormed);
    }

    public static Document createInstance(final File file, final String encoding) {
        return DocumentFactory.createInstance(file, encoding, null, true);
    }

    public static Document createInstance(final File file, final String encoding, final String password) {
        return DocumentFactory.createInstance(file, encoding, password, true);
    }

    public static Document createInstance(final File file, final String encoding, final String password,
            final boolean wellFormed) {
        if (file == null) {
            throw new IllegalArgumentException();
        }
        return DocumentFactory.factories.stream()
                .sorted((a, b) -> a.getPriority().ordinal() - b.getPriority().ordinal())
                .map(IDocumentClass::newInstance)
                .filter(x -> x.open(file, encoding, password, wellFormed))
                .findFirst()
                .orElseThrow(() -> new UnknownFormatConversionException(file.toString()));
    }

    private static List<IDocumentClass> factories;
    static {
        final Reflections reflections = new Reflections(PACKAGE_LOADER_PREFIX, new SubTypesScanner(false));
        DocumentFactory.factories = reflections.getSubTypesOf(IDocumentClass.class).stream()
                .map(clazz -> {
                    try {
                        return (IDocumentClass) clazz.getConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                        return null;
                    }
                })
                .filter(x -> x != null)
                .toList();
    }
}
