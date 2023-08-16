package com.github.romualdrousseau.any2json.base;

public interface Symbol {

    String getSymbol();

    boolean matchLiteral(String literal);
}
