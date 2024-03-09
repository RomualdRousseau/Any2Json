package com.github.romualdrousseau.any2json.event;

import java.io.PrintStream;

import com.github.romualdrousseau.any2json.Header;
import com.github.romualdrousseau.any2json.Sheet;
import com.github.romualdrousseau.any2json.SheetEvent;
import com.github.romualdrousseau.any2json.base.BaseTableGraph;
import com.github.romualdrousseau.any2json.base.DataTable;

public class TableGraphBuiltEvent extends SheetEvent {

    public TableGraphBuiltEvent(final Sheet source, final BaseTableGraph tableGraph) {
        super(source);
        this.tableGraph = tableGraph;
    }

    public BaseTableGraph getTableGraph() {
        return this.tableGraph;
    }

    public void dumpTableGraph(final PrintStream con) {
        con.println("============================== DUMP GRAPH ===============================");
        con.println(this.getSource().getName());
        this.walkThroughTableGraph(con, this.tableGraph, 0, 0);
        con.println("================================== END ==================================");
    }

    private int walkThroughTableGraph(final PrintStream con, final BaseTableGraph graph, final int indent, int counter) {
        if (!graph.isRoot()) {
            final StringBuffer out = new StringBuffer();

            for (int i = 0; i < indent; i++) {
                out.append("|- ");
            }

            for (final Header header : graph.getTable().headers()) {
                out.append(header.getName()).append(" ");
            }

            if (graph.getTable() instanceof DataTable) {
                out.append("DATA(");
            } else {
                out.append("META(");
            }
            out.append(graph.getTable().getFirstColumn()).append(", ");
            out.append(graph.getTable().getFirstRow()).append(", ");
            out.append(graph.getTable().getLastColumn()).append(", ");
            out.append(graph.getTable().getLastRow()).append(", ");
            out.append(graph.getTable().getLastRow() - graph.getTable().getFirstRow() + 1).append(", ");
            out.append(graph.getTable().getNumberOfRows());
            out.append(")");

            if (graph.getTable() instanceof DataTable) {
                out.append(" (").append(counter + 1).append(")");
                counter++;
            }

            con.println(out.toString());
        }

        for (final BaseTableGraph child : graph.children()) {
            counter = walkThroughTableGraph(con, child, indent + 1, counter);
        }

        return counter;
    }

    private final BaseTableGraph tableGraph;
}
