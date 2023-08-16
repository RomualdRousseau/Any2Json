package com.github.romualdrousseau.any2json.config;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.github.romualdrousseau.any2json.DocumentClass;
import com.github.romualdrousseau.any2json.TableParserClass;
import com.github.romualdrousseau.any2json.TagClassifierClass;

public class DynamicPackages {

    public final static String PACKAGE_LOADER_PREFIX = "com.github.romualdrousseau.any2json.loader";
    public final static String PACKAGE_PARSER_PREFIX = "com.github.romualdrousseau.any2json.parser";
    public final static String PACKAGE_CLASSIFIER_PREFIX = "com.github.romualdrousseau.any2json.classifier";

    public static List<DocumentClass> GetDocumentFactories() {
        return DocumentFactories;
    }

    public static Optional<TableParserClass> GetElementParserFactory() {
        return ElementParserFactory;
    }

    public static Optional<TagClassifierClass> GetTagClassifierFactory() {
        return TagClassifierFactory;
    }

    static {
        DocumentFactories = FindClasses(PACKAGE_LOADER_PREFIX, DocumentClass.class)
                .map(DynamicPackages::<DocumentClass>CreateInstance)
                .filter(x -> x != null)
                .sorted((a, b) -> a.getPriority().ordinal() - b.getPriority().ordinal())
                .toList();

        ElementParserFactory = FindClasses(PACKAGE_PARSER_PREFIX, TableParserClass.class)
                .map(DynamicPackages::<TableParserClass>CreateInstance)
                .findFirst();

        TagClassifierFactory = FindClasses(PACKAGE_CLASSIFIER_PREFIX, TagClassifierClass.class)
                .map(DynamicPackages::<TagClassifierClass>CreateInstance)
                .findFirst();
    }

    private static <T> Stream<Class<? extends T>> FindClasses(final String prefix, final Class<T> type) {
        final Reflections reflections = new Reflections(prefix, new SubTypesScanner(false));
        return reflections.getSubTypesOf(type).stream();
    }

    private static <T> T CreateInstance(final Class<? extends T> clazz) {
        try {
            return (T) clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    private static List<DocumentClass> DocumentFactories;
    private static Optional<TableParserClass> ElementParserFactory;
    private static Optional<TagClassifierClass> TagClassifierFactory;
}

