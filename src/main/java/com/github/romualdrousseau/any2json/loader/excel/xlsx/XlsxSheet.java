package com.github.romualdrousseau.any2json.loader.excel.xlsx;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import com.github.romualdrousseau.any2json.Sheet;
import com.github.romualdrousseau.any2json.intelli.IntelliSheet;
import com.github.romualdrousseau.shuju.util.StringUtility;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XlsxSheet extends IntelliSheet {

    public XlsxSheet(final String name, final InputStream sheetData, final SharedStrings sharedStrings,
            final StylesTable styles) {
        this.name = name;
        this.sheetData = sheetData;
        this.sharedStrings = sharedStrings;
        this.styles = styles;
    }

    public Sheet ensureDataLoaded() {
        if (this.dataLoaded) {
            return this;
        }
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            XMLReader parser = parserFactory.newSAXParser().getXMLReader();
            parser.setContentHandler(new ContentHandler());
            parser.parse(new InputSource(this.sheetData));
            return this;

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
            return this;

        } finally {
            try {
                this.dataLoaded = true;
                this.sheetData.close();
            } catch (IOException ignore) {
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    protected int getInternalLastColumnNum(int rowIndex) {
        return this.rows.get(rowIndex).getLastColumnNum();
    }

    @Override
    protected int getInternalLastRowNum() {
        return this.rows.size() - 1;
    }

    @Override
    protected boolean hasInternalCellDataAt(int colIndex, int rowIndex) {
        final XlsxRow row = this.rows.get(rowIndex);
        return row.cells().get(colIndex).getValue() != null;
    }

    @Override
    protected boolean hasInternalCellDecorationAt(int colIndex, int rowIndex) {
        final XlsxRow row = this.rows.get(rowIndex);
        return row.cells().get(colIndex).isDecorated();
    }

    @Override
    protected String getInternalCellDataAt(int colIndex, int rowIndex) {
        final XlsxRow row = this.rows.get(rowIndex);
        return row.cells().get(colIndex).getValue();
    }

    @Override
    protected int getInternalMergeAcross(final int colIndex, final int rowIndex) {
        if (mergedRegions.size() == 0) {
            return 1;
        }

        int numberOfCells = 0;
        for (final CellRangeAddress region : mergedRegions) {
            if (region.isInRange(rowIndex, colIndex)) {
                numberOfCells = (region.getLastColumn() - region.getFirstColumn());
                break;
            }
        }

        return numberOfCells + 1;
    }

    @Override
    protected int getInternalMergeDown(final int colIndex, final int rowIndex) {
        if (this.mergedRegions.size() == 0) {
            return 0;
        }

        int numberOfCells = 0;
        for (final CellRangeAddress region : mergedRegions) {
            if (region.getLastRow() > region.getFirstRow() && region.isInRange(rowIndex, colIndex)
                    && rowIndex > region.getFirstRow()) {
                numberOfCells = region.getLastRow() - region.getFirstRow();
                break;
            }
        }

        return numberOfCells;
    }

    private class ContentHandler extends DefaultHandler {

        @Override
        public void startElement(final String uri, final String localName, final String name,
                final Attributes attributes) {
            if ("row".equals(name)) {
                assert (attributes.getValue("r") != null) : "Row malformed without ref";
                this.fillMissingRows(Integer.valueOf(attributes.getValue("r")) - 1);
                this.row = new XlsxRow();
                this.row.setHeight(this.getRowHeightFromString(attributes.getValue("ht")));
                this.prevCell = null;
                this.currCell = null;
            } else if ("c".equals(name)) {
                assert (attributes.getValue("r") != null) : "Cell malformed without ref";
                this.prevCell = this.currCell;
                this.currCell = new Cell();
                this.currCell.address = new CellAddress(attributes.getValue("r"));
                this.currCell.type = this.getCellTypeFromString(attributes.getValue("t"));
                this.currCell.style = this.getCellStyleFromString(attributes.getValue("s"));
            } else if ("v".equals(name)) {
                this.startValue = true;
                this.inlineStr = false;
                this.currCell.value = "";
            } else if ("t".equals(name)) {
                this.startValue = true;
                this.inlineStr = true;
                this.currCell.value = "";
            } else if ("mergeCell".equals(name) && attributes.getValue("ref") != null) {
                mergedRegions.add(CellRangeAddress.valueOf(attributes.getValue("ref")));
            }
        }

        @Override
        public void endElement(final String uri, final String localName, final String name) {
            if ("row".equals(name)) {
                rows.add(this.row);
            } else if ("c".equals(name)) {
                this.fillMissingCells();
                XlsxCell cell = XlsxCell.Empty;
                if (this.processCellData(this.currCell)) {
                    cell = new XlsxCell();
                    cell.setDecorated(this.currCell.decorated);
                    cell.setValue(this.currCell.value);
                }
                this.row.addCell(cell);
                this.row.setLastColumnNum(Math.max(this.row.getLastColumnNum(), this.currCell.address.getColumn()));
            } else if ("v".equals(name)) {
                this.startValue = false;
            } else if ("t".equals(name)) {
                this.startValue = false;
            }
        }

        @Override
        public void characters(final char ch[], final int start, final int length) throws SAXException {
            if (this.startValue) {
                this.currCell.value += new String(ch, start, length);
            }
        }

        private void fillMissingRows(int n) {
            while (rows.size() < n) {
                rows.add(new XlsxRow());
            }
        }

        private float getRowHeightFromString(final String heightStr) {
            if (heightStr != null) {
                return Float.valueOf(heightStr) * 4.0f / 3.0f; // Conversion in pixel
            } else {
                return XlsxRow.DEFAULT_HEIGHT;
            }
        }

        private CellType getCellTypeFromString(final String typeStr) {
            if (typeStr == null) {
                return CellType.BLANK;
            } else if (typeStr.equals("s") || typeStr.equals("inlineStr")) {
                return CellType.STRING;
            } else if (typeStr.equals("b")) {
                return CellType.BOOLEAN;
            } else if (typeStr.equals("e")) {
                return CellType.ERROR;
            } else {
                return CellType.NUMERIC;
            }
        }

        private XSSFCellStyle getCellStyleFromString(final String styleStr) {
            if (styleStr != null) {
                return styles.getStyleAt(Integer.valueOf(styleStr));
            } else if (styles.getNumCellStyles() > 0) {
                return styles.getStyleAt(0);
            } else {
                return null;
            }
        }

        private void fillMissingCells() {
            final int prevColumn = (this.prevCell == null) ? 0 : (this.prevCell.address.getColumn() + 1);
            for (int i = prevColumn; i < this.currCell.address.getColumn(); i++) {
                this.row.addCell(XlsxCell.Empty);
            }
        }

        private boolean processCellData(final Cell cell) {
            cell.type = (cell.value == null && cell.type.equals(CellType.NUMERIC)) ? CellType.BLANK : cell.type;
            cell.type = (cell.value != null && cell.type.equals(CellType.BLANK)) ? CellType.NUMERIC : cell.type;

            if (cell.type.equals(CellType.STRING)) {
                if (this.inlineStr) {
                    cell.value = StringUtility.cleanToken(cell.value);
                } else {
                    cell.value = StringUtility
                            .cleanToken(sharedStrings.getItemAt(Integer.valueOf(cell.value)).toString());
                }
            } else if (cell.type.equals(CellType.BOOLEAN)) {
                cell.value = cell.value.equals("1") ? "TRUE" : "FALSE";

            } else if (cell.type.equals(CellType.NUMERIC)) {
                try {
                    final double d = Double.valueOf(cell.value);
                    if (DateUtil.isADateFormat(cell.style.getDataFormat(), cell.style.getDataFormatString())
                            && DateUtil.isValidExcelDate(d)) {
                        cell.value = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getJavaDate(d));
                    }
                } catch (NumberFormatException x) {
                    cell.type = CellType.STRING;
                    cell.value = StringUtility.cleanToken(cell.value);
                }

            } else if (cell.type.equals(CellType.BLANK)) {
                cell.value = "";
            }

            cell.decorated = hasDecoration(cell);

            return this.hasData(cell) || cell.decorated;
        }

        private boolean hasData(final Cell cell) {
            if (!cell.type.equals(CellType.BLANK) && cell.value != null && !cell.value.isEmpty()) {
                return true;
            }

            if (cell.style == null) {
                return false;
            }

            // Keep cell with borders
            if (!cell.style.getBorderLeft().equals(BorderStyle.NONE)
                    && !cell.style.getBorderRight().equals(BorderStyle.NONE)
                    && !cell.style.getBorderTop().equals(BorderStyle.NONE)
                    && !cell.style.getBorderBottom().equals(BorderStyle.NONE)) {
                return true;
            }

            // Keep cell with a colored (not automatic and not white) pattern
            final XSSFColor bkcolor = (XSSFColor) cell.style.getFillBackgroundColorColor();
            if (bkcolor != null && bkcolor.getIndexed() != IndexedColors.AUTOMATIC.index
                    && (bkcolor.getARGBHex() == null || !bkcolor.getARGBHex().equals("FFFFFFFF"))) {
                return true;
            }

            // Keep cell with a colored (not automatic and not white) background
            final XSSFColor fgcolor = (XSSFColor) cell.style.getFillForegroundColorColor();
            if (fgcolor != null && fgcolor.getIndexed() != IndexedColors.AUTOMATIC.index
                    && (fgcolor.getARGBHex() == null || !fgcolor.getARGBHex().equals("FFFFFFFF"))) {
                return true;
            }

            return false;
        }

        private boolean hasDecoration(final Cell cell) {
            if (cell.style == null) {
                return false;
            }

            // Keep cell with borders
            if (!cell.style.getBorderLeft().equals(BorderStyle.NONE)
                    && !cell.style.getBorderRight().equals(BorderStyle.NONE)
                    && !cell.style.getBorderTop().equals(BorderStyle.NONE)
                    && !cell.style.getBorderBottom().equals(BorderStyle.NONE)) {
                return true;
            }

            // Keep cell with a colored (not automatic and not white) pattern
            final XSSFColor bkcolor = (XSSFColor) cell.style.getFillBackgroundColorColor();
            if (bkcolor != null && bkcolor.getIndexed() != IndexedColors.AUTOMATIC.index
                    && (bkcolor.getARGBHex() == null || !bkcolor.getARGBHex().equals("FFFFFFFF"))) {
                return true;
            }

            // Keep cell with a colored (not automatic and not white) background
            final XSSFColor fgcolor = (XSSFColor) cell.style.getFillForegroundColorColor();
            if (fgcolor != null && fgcolor.getIndexed() != IndexedColors.AUTOMATIC.index
                    && (fgcolor.getARGBHex() == null || !fgcolor.getARGBHex().equals("FFFFFFFF"))) {
                return true;
            }

            return false;
        }

        private class Cell {
            CellAddress address;
            CellType type;
            CellStyle style;
            String value;
            boolean decorated;
        }

        private XlsxRow row;
        private boolean startValue;
        private boolean inlineStr;
        private Cell currCell;
        private Cell prevCell;
    }

    private final String name;
    private final InputStream sheetData;
    private final StylesTable styles;
    private final SharedStrings sharedStrings;
    private final ArrayList<CellRangeAddress> mergedRegions = new ArrayList<CellRangeAddress>();
    private final ArrayList<XlsxRow> rows = new ArrayList<XlsxRow>();
    private boolean dataLoaded;
}
