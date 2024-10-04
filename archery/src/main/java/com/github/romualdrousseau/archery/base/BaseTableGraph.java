package com.github.romualdrousseau.archery.base;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.github.romualdrousseau.archery.TableGraph;

public class BaseTableGraph implements TableGraph {

    public BaseTableGraph() {
        this.table = null;
        this.parent = null;
    }

    public BaseTableGraph(final BaseTable table) {
        this.table = table;
        this.parent = null;
    }

    @Override
    public boolean isRoot() {
        return this.parent == null;
    }

    @Override
    public BaseTable getTable() {
        return this.table;
    }

    @Override
    public BaseTableGraph getParent() {
        return this.parent;
    }

    @Override
    public List<BaseTableGraph> children() {
        return this.children;
    }

    @Override
    public void parse(Consumer<TableGraph> func) {
        for (final var child : this.children()) {
            func.accept(child);
        }
        for (final var child : this.children()) {
            child.parse(func);
        }
    }

    @Override
    public void parseIf(Consumer<TableGraph> func, Predicate<TableGraph> pred) {
        this.parse(e -> {
            if (pred.test(e)) {
                func.accept(e);
            }
        });
    }

    public void addChild(final BaseTableGraph child) {
        child.parent = this;
        this.children.add(child);

        this.children.sort(new Comparator<BaseTableGraph>() {
            @Override
            public int compare(final BaseTableGraph o1, final BaseTableGraph o2) {
                return o1.getTable().getFirstRow() - o2.getTable().getFirstRow();
            }
        });
    }

    public BaseHeader findClosestHeader(final BaseHeader abstractHeader) {
        if (this.table == null) {
            return abstractHeader;
        }
        return Optional
                .ofNullable(this.table.findClosestHeader(abstractHeader))
                .orElseGet(() -> this.parent.findClosestHeader(abstractHeader));
    }

    private final BaseTable table;
    private BaseTableGraph parent;
    private final LinkedList<BaseTableGraph> children = new LinkedList<>();
}
