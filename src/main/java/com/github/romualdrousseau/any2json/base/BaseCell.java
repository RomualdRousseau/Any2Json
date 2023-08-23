package com.github.romualdrousseau.any2json.base;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.github.romualdrousseau.any2json.Cell;
import com.github.romualdrousseau.shuju.strings.StringUtils;
import com.github.romualdrousseau.shuju.types.Tensor;

public class BaseCell implements Cell, Symbol {

    public final static BaseCell Empty = new BaseCell("", 0, 1, null);

    public final static BaseCell EndOfRow = new BaseCell("", 0, 0, null);

    public final static BaseCell EndOfStream = new BaseCell("", 0, 0, null);

    public BaseCell(final String value, BaseCell cell) {
        this(value, cell.getColumnIndex(),
                cell.getMergedCount(), cell.getRawValue(), cell.getSheet());
    }

    public BaseCell(final String value, final int colIndex, final int mergedCount,
            final BaseSheet sheet) {
        this(value, colIndex, mergedCount, value, sheet);
    }

    public BaseCell(final String value, final int colIndex, final int mergedCount, final String rawValue, final BaseSheet sheet) {
        this.colIndex = colIndex;
        this.mergedCount = mergedCount;
        this.rawValue = (rawValue == null) ? "" : rawValue;
        this.sheet = sheet;
        this.setValue(value);
    }

    @Override
    public boolean hasValue() {
        return !StringUtils.isFastBlank(this.value);
    }

    @Override
    public String getValue() {
        return (this.value == null) ? "" : this.value;
    }

    @Override
    public String getEntitiesAsString() {
        return String.join("|", this.entities());
    }

    @Override
    public List<String> entities() {
        return this.entityList;
    }

    @Override
    public String getSymbol() {
        if (this == BaseCell.EndOfStream) {
            return "";
        } else if (this == BaseCell.EndOfRow) {
            return "$";
        } else if (!this.hasValue()) {
            return "s";
        } else if (this.entityList.size() > 0) {
            return "e";
        } else {
            return "v";
        }
    }

    @Override
    public boolean matchLiteral(final String literal) {
        return this.entityList.stream().anyMatch(x -> x.equalsIgnoreCase(literal));
    }

    public BaseSheet getSheet() {
        return this.sheet;
    }

    public String getRawValue() {
        return this.rawValue;
    }

    public int getMergedCount() {
        return this.mergedCount;
    }

    public int getColumnIndex() {
        return this.colIndex;
    }

    public Tensor getEntityVector() {
        return this.entityVector;
    }

    public boolean isPivotHeader() {
        return this.getPivotEntityAsString().isPresent();
    }

    public Optional<String> getPivotEntityAsString() {
        if (this.sheet != null) {
            return this.entityList.stream().filter(x -> this.sheet.getDocument().getModel().getPivotEntityList().contains(x)).findFirst();
        } else {
            return Optional.empty();
        }
    }

    public void setValue(final String value) {
        this.value = value;
        if (this.sheet != null) {
            final List<String> entityList = this.sheet.getDocument().getModel().getEntityList();
            this.entityVector = this.sheet.getDocument().getModel().toEntityVector(this.value);
            this.entityList = IntStream.range(0, this.entityVector.size).boxed().filter(i -> this.entityVector.data[i] > 0.0f).map(i -> entityList.get(i)).toList();
        } else {
            this.entityVector = Tensor.Null;
            this.entityList = Collections.emptyList();
        }
    }

    private final BaseSheet sheet;
    private final int colIndex;
    private final int mergedCount;
    private final String rawValue;

    private String value;
    private Tensor entityVector;
    private List<String> entityList;
}
