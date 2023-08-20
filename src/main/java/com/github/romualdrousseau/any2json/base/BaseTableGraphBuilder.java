package com.github.romualdrousseau.any2json.base;

import java.util.List;

public class BaseTableGraphBuilder {

    public static BaseTableGraph Build(final List<MetaTable> metaTables, final List<DataTable> dataTables) {
        final BaseTableGraph root = new BaseTableGraph();

        for (final Visitable e : metaTables) {
            e.setVisited(false);
        }

        // First attach all not snapped metaTables to the root nodes
        for (final MetaTable metaTable : metaTables) {
            if (!isJoint(metaTable, dataTables)) {
                root.addChild(new BaseTableGraph(metaTable));
                metaTable.setVisited(true);
            }
        }

        // Second attach all snapped metaTables to the closest nodes
        for (final MetaTable metaTable : metaTables) {
            if (metaTable.isVisited()) {
                continue;
            }

            final BaseTableGraph parent = findClosestMetaGraph(root, metaTable, 0, 0);
            parent.addChild(new BaseTableGraph(metaTable));
            metaTable.setVisited(true);
        }

        // Third attach datatables to the closest metadatas
        for (final DataTable dataTable : dataTables) {
            final BaseTableGraph parent = findClosestMetaGraph(root, dataTable, 0, 1);
            parent.addChild(new BaseTableGraph(dataTable));
        }

        return root;
    }

    private static boolean isJoint(final MetaTable metaTable, final List<DataTable> dataTables) {
        for (final DataTable dataTable : dataTables) {
            if (distanceBetweenTables(metaTable, dataTable) == 0) {
                return true;
            }
        }
        return false;
    }

    private static BaseTableGraph findClosestMetaGraph(final BaseTableGraph root, final BaseTable table, final int level,
            final int maxLevel) {
        BaseTableGraph result = root;

        if (level > maxLevel) {
            return result;
        }

        double minDist = Double.MAX_VALUE;
        for (final BaseTableGraph child : root.children()) {
            if (!(child.getTable() instanceof MetaTable)) {
                continue;
            }

            final BaseTableGraph grandChild = findClosestMetaGraph(child, table, level + 1, maxLevel);
            final double dist1 = distanceBetweenTables(grandChild.getTable(), table);
            final double dist2 = distanceBetweenTables(child.getTable(), table);

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

    private static double distanceBetweenTables(final BaseTable table1, final BaseTable table2) {
        final int vx = table2.getFirstColumn() - table1.getFirstColumn();
        final int vy = table2.getFirstRow() - table1.getLastRow() - 1;
        if (vx >= 0 && vy >= 0) {
            return vx + vy;
        } else {
            return Double.MAX_VALUE;
        }
    }
}
