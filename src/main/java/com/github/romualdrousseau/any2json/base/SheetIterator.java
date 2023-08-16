package com.github.romualdrousseau.any2json.base;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;

import com.github.romualdrousseau.any2json.Document;
import com.github.romualdrousseau.any2json.Sheet;

public class SheetIterator implements Iterator<Sheet>
{
	public SheetIterator(final Document document) {
		this.document = document;
		this.currRowIdx = 0;
	}

	public boolean hasNext() {
		return this.currRowIdx < this.document.getNumberOfSheets();
	}

	public Sheet next() {
		return this.document.getSheetAt(this.currRowIdx++);
	}

    public Spliterator<Sheet> spliterator() {
        return Spliterators.spliterator(this, this.document.getNumberOfSheets(), Spliterator.IMMUTABLE);
    }

	private final Document document;
	private int currRowIdx;
}
