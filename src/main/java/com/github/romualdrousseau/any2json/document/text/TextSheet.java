package com.github.romualdrousseau.any2json.document.text;

import java.util.List;
import java.util.ArrayList;

import com.github.romualdrousseau.any2json.Sheet;
import com.github.romualdrousseau.any2json.Table;
import com.github.romualdrousseau.any2json.ITable;

class TextSheet extends Sheet
{
	public TextSheet(String name, Table table) {
		this.name = name;
		this.table = table;
	}

	public String getName() {
		return this.name;
	}

	public ITable getTable() {
        this.table.enableMetaTable(false);
		return this.table;
	}

	public ITable findTable(int headerColumns, int headerRows) {
		return this.table;
    }

    public List<ITable> findTables(int headerColumns, int headerRows) {
        ArrayList<ITable> result = new ArrayList<ITable>();
        result.add(this.table);
        return result;
    }

	private String name;
	private Table table;
}
