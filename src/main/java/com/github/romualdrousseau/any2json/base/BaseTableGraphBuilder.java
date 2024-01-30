package com.github.romualdrousseau.any2json.base;

import java.util.List;

import com.github.romualdrousseau.any2json.ReadingDirection;

public class BaseTableGraphBuilder {

    public static BaseTableGraph build(final List<MetaTable> metaTables, final List<DataTable> dataTables,
            final ReadingDirection readingDirection) {
        final var root = new BaseTableGraph();

        for (final Visitable e : metaTables) {
            e.setVisited(false);
        }

        // First attach all not snapped metaTables to the root nodes

        for (final MetaTable metaTable : metaTables) {
            if (!isSnapped(metaTable, dataTables, readingDirection)) {
                root.addChild(new BaseTableGraph(metaTable));
                metaTable.setVisited(true);
            }
        }

        // Second attach all snapped metaTables to the closest nodes

        for (final MetaTable metaTable : metaTables) {
            if (metaTable.isVisited()) {
                continue;
            }

            final var parent = findClosestMetaGraph(root, metaTable, 0, 0, readingDirection);
            parent.addChild(new BaseTableGraph(metaTable));
            metaTable.setVisited(true);
        }

        // Third attach datatables to the closest metadatas

        for (final DataTable dataTable : dataTables) {
            final var parent = findClosestMetaGraph(root, dataTable, 0, 1, readingDirection);
            parent.addChild(new BaseTableGraph(dataTable));
        }

        return root;
    }

    private static boolean isSnapped(final MetaTable metaTable, final List<DataTable> dataTables,
            final ReadingDirection readingDirection) {
        for (final var dataTable : dataTables) {
            if (readingDirection.distanceBetweenTables(metaTable, dataTable) == 0) {
                return true;
            }
        }
        return false;
    }

    private static BaseTableGraph findClosestMetaGraph(final BaseTableGraph root, final BaseTable table,
            final int level, final int maxLevel, final ReadingDirection readingDirection) {
        BaseTableGraph result = root;

        if (level > maxLevel) {
            return result;
        }

        var minDist = Double.MAX_VALUE;
        for (final var child : root.children()) {
            if (!(child.getTable() instanceof MetaTable)) {
                continue;
            }

            final var grandChild = findClosestMetaGraph(child, table, level + 1, maxLevel, readingDirection);
            final var dist1 = readingDirection.distanceBetweenTables(grandChild.getTable(), table);
            final var dist2 = readingDirection.distanceBetweenTables(child.getTable(), table);

            if (dist1 < dist2) {
                if (dist1 < minDist) {
                    minDist = dist1;
                    result = grandChild;
                }
            } else {
                if (dist2 < minDist) {
                    minDist = dist2;
                    result = child;
                }
            }
        }

        return result;
    }
}
