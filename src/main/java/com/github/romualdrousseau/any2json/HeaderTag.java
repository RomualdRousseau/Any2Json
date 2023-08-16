package com.github.romualdrousseau.any2json;

public class HeaderTag {

    public final static HeaderTag None = new HeaderTag("none");

    public HeaderTag(final String value) {
		this.value = value;
    }

	public String getValue() {
		return this.value;
	}

    public boolean isUndefined() {
		return this.value == null || value.equalsIgnoreCase(HeaderTag.None.getValue());
	}

    public boolean equals(final HeaderTag other) {
        return !this.isUndefined() && !other.isUndefined() && this.value.equalsIgnoreCase(other.value);
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof HeaderTag && this.equals((HeaderTag) o);
    }

	private final String value;
}
