package com.github.romualdrousseau.any2json.base;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class BaseTableGraph {

    public BaseTableGraph() {
        this.table = null;
        this.parent = null;
    }

    public BaseTableGraph(final BaseTable table) {
        this.table = table;
        this.parent = null;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public BaseTable getTable() {
        return this.table;
    }

    public BaseTableGraph getParent() {
        return this.parent;
    }

    public List<BaseTableGraph> children() {
        return this.children;
    }

    public void addChild(final BaseTableGraph child) {
        child.parent = this;
        this.children.add(child);

        this.children.sort(new Comparator<BaseTableGraph>() {
            @Override
            public int compare(final BaseTableGraph o1, final BaseTableGraph o2) {
                return o1.table.getFirstRow() - o2.table.getFirstRow();
            }
        });
    }

    private final BaseTable table;
    private BaseTableGraph parent;
    private final LinkedList<BaseTableGraph> children = new LinkedList<BaseTableGraph>();
}
