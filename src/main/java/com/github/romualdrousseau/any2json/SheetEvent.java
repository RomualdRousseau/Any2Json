package com.github.romualdrousseau.any2json;

public abstract class SheetEvent {

    public SheetEvent(final Sheet source) {
        this.source = source;
        this.canceled = false;
    }

    public Sheet getSource() {
        return this.source;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCancel(final boolean flag) {
        this.canceled = flag;
    }

    private final Sheet source;
    private boolean canceled;
}
