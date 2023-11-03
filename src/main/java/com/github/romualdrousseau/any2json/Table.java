package com.github.romualdrousseau.any2json;

import java.io.Closeable;

public interface Table extends Closeable {

    Sheet getSheet();

    int getNumberOfColumns();

    int getNumberOfRows();

    Row getRowAt(final int rowIndex);

    Iterable<Row> rows();

    int getNumberOfHeaders();

    Iterable<String> getHeaderNames();

    Header getHeaderAt(final int i);

    Iterable<Header> headers();

    int getNumberOfHeaderTags();

    Iterable<Header> headerTags();

    void updateHeaderTags();
}
