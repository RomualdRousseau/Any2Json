package com.github.romualdrousseau.any2json.base;

import java.util.Iterator;

import com.github.romualdrousseau.any2json.Document;
import com.github.romualdrousseau.any2json.Sheet;

public class SheetIterable implements Iterable<Sheet>
{
	public SheetIterable(final Document document) {
		this.document = document;
	}

	public Iterator<Sheet> iterator() {
		return new SheetIterator(this.document);
	}

	private final Document document;
}
