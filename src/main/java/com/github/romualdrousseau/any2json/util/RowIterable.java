package com.github.romualdrousseau.any2json.util;

import java.util.Iterator;

import com.github.romualdrousseau.any2json.Row;
import com.github.romualdrousseau.any2json.base.AbstractTable;

public class RowIterable implements Iterable<Row>
{
	public RowIterable(AbstractTable table) {
		this.table = table;
	}

	public Iterator<Row> iterator() {
		return new RowIterator(this.table);
	}

	private AbstractTable table;
}
