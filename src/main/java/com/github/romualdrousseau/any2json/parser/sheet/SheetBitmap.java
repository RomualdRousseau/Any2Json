package com.github.romualdrousseau.any2json.parser.sheet;

import com.github.romualdrousseau.any2json.base.BaseSheet;
import com.github.romualdrousseau.shuju.cv.ISearchBitmap;

public class SheetBitmap implements ISearchBitmap
{
    public SheetBitmap(final int columns, final int rows) {
        this.width = columns + 1;
        this.height = rows + 1;
        this.data = new byte[this.height][(this.width / 8) + 1];
    }

    public SheetBitmap(final BaseSheet sheet, final int columns, final int rows) {
        this.width = columns + 1;
        this.height = rows + 1;
        this.data = new byte[this.height][(this.width / 8) + 1];
        this.convertSheetToBitmap(sheet);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int get(final int x, final int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            return 0;
        }
        int off = x / 8;
        int mod = x % 8;
        return (this.data[y][off] & (1 << mod)) > 0 ? 1 : 0;
    }

    public void set(final int x, final int y, final int v) {
        int off = x / 8;
        int mod = x % 8;
        if(v == 0) {
            this.data[y][off] &= ~(1 << mod);
        } else {
            this.data[y][off] |= (1 << mod);
        }
    }

    public ISearchBitmap clone() {
        final SheetBitmap result = new SheetBitmap(width, height);
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < (this.width / 8) + 1; x++) {
                result.data[y][x] = this.data[y][x];
            }
        }
        return result;
    }

    private void convertSheetToBitmap(final BaseSheet sheet) {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width;) {
                final int n = sheet.getNumberOfMergedCellsAt(x, y);
                if (sheet.hasCellDataAt(x, y)) {
                    for (int k = 0; k < n && (x + k < this.width); k++) {
                        int off = (x + k) / 8;
                        int mod = (x + k) % 8;
                        this.data[y][off] |= (1 << mod);
                    }
                }
                x += n;
            }
        }
    }

    private final int width;
    private final int height;
    private final byte[][] data;
}
