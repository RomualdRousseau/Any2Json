import com.github.romualdrousseau.shuju.nlp.*;
import com.github.romualdrousseau.shuju.json.*;

import com.github.romualdrousseau.any2json.ITagClassifier;
import com.github.romualdrousseau.any2json.classifiers.*;

import com.github.romualdrousseau.any2json.*;
import com.github.romualdrousseau.any2json.base.*;
import com.github.romualdrousseau.any2json.intelli.*;
import com.github.romualdrousseau.any2json.event.*;
import com.github.romualdrousseau.any2json.intelli.header.*;
import com.github.romualdrousseau.any2json.classifiers.layex.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.util.List;
import java.awt.event.KeyEvent;

final String[] metaLayexes = { "(v.$)+" };

final String[] dataLayexes = {
  //"(()(eeE$))((veE$)(eeE$)*)+(veE$)?",
  //"(()(vvE$))([(SeE$)[[veE$](eeE$)+]+]+)+()",
  "((v.*$)(vS.+$))((ve$)(.{3,}$)+)+(.{2}$)?",
  "(()(ES.+$))((e.{2,}$)(s.{2,}$)+)+(.{2}$)?",
  "(()(ES.+$))((s.{3,}$)(S.{2,}$)*)+(.{2}$)?",
  "[(([vs*$]?[e.*$])?(ES.+$))(()(.{3,}$))+(.{2}$)?]+",
  "((VV.+$)?(ES.+$))(()(.{3,}$))+(.{2}$)?"
};

ClassifierFactory classifier;
int scrollSpeed;
int gridSize;

volatile boolean documentLoaded = true;
PGraphics documentImage;
int documentTopY;

void configure() {
  classifier = new LayexAndNetClassifierBuilder()
    .setNgramList(new NgramList(JSON.loadJSONObject(dataPath("ngrams.json"))))
    .setEntityList(new RegexList(JSON.loadJSONObject(dataPath("entities.json"))))
    .setStopWordList(new StopWordList(JSON.loadJSONArray(dataPath("stopwords.json"))))
    .setTagList(new com.github.romualdrousseau.shuju.nlp.StringList(JSON.loadJSONObject(dataPath("tags.json"))))
    .setRequiredTagList(new String[] {"quantity", "productName"})
    .setPivotEntityList(new String[] {"date"})
    .setMetaLayexes(metaLayexes)
    .setDataLayexes(dataLayexes)
    .build();

  scrollSpeed = 100; // 100px per scroll

  gridSize = 10; // 10px
}

void setup() {
  size(1000, 800);
  noSmooth();
  frameRate(20);

  configure();

  buildEmptyImage();
}

void draw() {
  if (!documentLoaded) {
    return;
  }

  background(51);
  image(documentImage, 0, -documentTopY);
  displayHUD();
}

void fileSelected(File selection) {
  if (selection != null) {
    noLoop();
    documentLoaded = false;
    loadDocument(selection.getAbsolutePath());
    documentTopY = 0;
    documentLoaded = true;
    loop();
  }
}

void keyPressed() {
  if (keyCode == KeyEvent.VK_F3) {
    selectInput("Select a file to process:", "fileSelected");
  }
  if (keyCode == KeyEvent.VK_HOME) {
    documentTopY = 0;
  }
  if (keyCode == KeyEvent.VK_END) {
    documentTopY = max(0, documentImage.height - height + 17);
  }
  if (keyCode == KeyEvent.VK_PAGE_UP ) {
    documentTopY = (int) constrain(documentTopY - height, -gridSize, max(0, documentImage.height - height + 17) + gridSize);
  }
  if (keyCode == KeyEvent.VK_PAGE_DOWN ) {
    documentTopY = (int) constrain(documentTopY + height, -gridSize, max(0, documentImage.height - height + 17) + gridSize);
  }
}

void mouseWheel(MouseEvent event) {
  documentTopY = (int) constrain(documentTopY + event.getCount() * scrollSpeed, -gridSize, max(0, documentImage.height - height + 17) + gridSize);
}

void loadDocument(String filePath) {
  println("Loading document ... ");

  File tempFile = null;
  try {
    tempFile = File.createTempFile("temp", null);
    tempFile.deleteOnExit();
    Path oldFile = Paths.get(filePath);
    Path newFile = tempFile.toPath();
    Files.copy(oldFile, newFile, StandardCopyOption.REPLACE_EXISTING);
  }
  catch (IOException e) {
    e.printStackTrace();
  }

  if (tempFile == null) {
    return;
  }

  Document document = DocumentFactory.createInstance(tempFile.getAbsolutePath(), "UTF-8", null);

  Sheet sheet = document.getSheetAt(0);
  sheet.addSheetListener(new SheetListener() {
    public void stepCompleted(SheetEvent e) {
      buildImage(e);
    }
  }
  );

  com.github.romualdrousseau.any2json.Table table = sheet.getTable(classifier);
  println("Tables loaded.");
  println("done.");

  document.close();
}

void buildEmptyImage() {
  int dx = width / classifier.getLayoutClassifier().get().getSampleCount();
  int dy = gridSize;

  documentImage = createGraphics(width, height);

  documentImage.beginDraw();
  documentImage.stroke(128);
  documentImage.strokeWeight(1);
  for (int y = 0; y < height / dy; y++) {
    for (int x = 0; x < width / dx; x++) {
      documentImage.fill(0);
      documentImage.rect(x * dx, y * dy, dx, dy);
    }
  }
  documentImage.endDraw();
}

void buildImage(SheetEvent e) {
  AbstractSheet sheet = (AbstractSheet) e.getSource();
  int dx = width / classifier.getLayoutClassifier().get().getSampleCount();
  int maxRows = Math.min(sheet.getLastRowNum() + 1, 5000);

  if (e instanceof BitmapGeneratedEvent) {
    // Max rows set to 5000 to prevent heap overflow
    documentImage = createGraphics(width, maxRows * gridSize);
    documentImage.beginDraw();
    documentImage.stroke(128);
    documentImage.strokeWeight(1);

    SheetBitmap bitmap = ((BitmapGeneratedEvent) e).getBitmap();
    if (bitmap == null) {
      for (int y = 0; y <= sheet.getLastRowNum(); y++) {
        for (int x = 0; x < classifier.getLayoutClassifier().get().getSampleCount(); x++) {
          documentImage.fill(color(255 * ((x <= sheet.getLastColumnNum(y)) ? 1 : 0)));
          documentImage.rect(x * dx, y * gridSize, dx, gridSize);
        }
      }
    } else {
      for (int y = 0; y < bitmap.getHeight(); y++) {
        for (int x = 0; x < bitmap.getWidth(); x++) {
          documentImage.fill(color(255 * bitmap.get(x, y)));
          documentImage.rect(x * dx, y * gridSize, dx, gridSize);
        }
      }
    }

    documentImage.endDraw();
    println("Image generated.");
  }

  if (e instanceof AllTablesExtractedEvent) {
    documentImage.beginDraw();
    documentImage.textAlign(CENTER, CENTER);
    documentImage.textSize(gridSize - 2);

    for (AbstractTable table : ((AllTablesExtractedEvent) e).getTables()) {
      documentImage.strokeWeight(1);
      int y = 0;
      for (Row row : table.rows()) {
        int x = 0;
        for (Cell c : row.cells()) {
          BaseCell cell = (BaseCell) c;
          if (cell.hasValue()) {
            documentImage.stroke(0, 128, 255);
            documentImage.fill(color(255, 255, 255));
            documentImage.rect((table.getFirstColumn() + x) * dx, (table.getFirstRow() + y) * gridSize, cell.getMergedCount() * dx, gridSize);
            if (cell.getEntityString() != null) {
              documentImage.noStroke();
              documentImage.fill(color(0, 128, 255));
              documentImage.text(cell.getEntityString().substring(0, 2), (table.getFirstColumn() + x) * dx + cell.getMergedCount() * dx / 2, (table.getFirstRow() + y) * gridSize + gridSize / 2);
            }
          } else {
            documentImage.stroke(128);
            documentImage.fill(color(64, 64, 64));
            documentImage.rect((table.getFirstColumn() + x) * dx, (table.getFirstRow() + y) * gridSize, cell.getMergedCount() * dx, gridSize);
          }
          x += cell.getMergedCount();
        }
        y++;
        if(y >= maxRows) {
          break;
        }
      }

      documentImage.stroke(0, 0, 255);
      documentImage.strokeWeight(2);
      documentImage.noFill();
      documentImage.rect(table.getFirstColumn() * dx, table.getFirstRow() * gridSize, table.getNumberOfColumns() * dx, y * gridSize);
    }
    documentImage.endDraw();

    println("Tables extracted from image.");
  }

  if (e instanceof DataTableListBuiltEvent) {
    documentImage.beginDraw();
    documentImage.noStroke();

    for (DataTable table : ((DataTableListBuiltEvent) e).getDataTables()) {
      // meta
      documentImage.fill(color(255, 128, 0), 128);
      documentImage.rect(table.getFirstColumn() * dx + 1, table.getFirstRow() * gridSize + 1, table.getNumberOfColumns() * dx - 2, table.getFirstRowOffset() * gridSize - 1);
      // header
      documentImage.fill(color(0, 255, 0), 128);
      documentImage.rect(table.getFirstColumn() * dx + 1, (table.getFirstRow() + table.getHeaderRowOffset()) * gridSize + 1, table.getNumberOfColumns() * dx - 2, gridSize - 1);
      // data
      documentImage.fill(color(0, 255, 0), 64);
      documentImage.rect(table.getFirstColumn() * dx + 1, (table.getFirstRow() + table.getFirstRowOffset()) * gridSize + 1, table.getNumberOfColumns() * dx - 2, table.getNumberOfRows() * gridSize - 1);
      // rowgroups
      documentImage.fill(color(0, 255, 255), 128);
      for (RowGroup rowGroup : table.rowGroups()) {
        documentImage.rect(table.getFirstColumn() * dx + 1, (table.getFirstRow() + table.getFirstRowOffset() + rowGroup.getRow()) * gridSize + 1, table.getNumberOfColumns() * dx - 2, gridSize - 1);
      }
      // footer
      documentImage.fill(color(0, 0, 0), 64);
      documentImage.rect(table.getFirstColumn() * dx + 1, (table.getLastRow() + table.getLastRowOffset() + 1) * gridSize + 1, table.getNumberOfColumns() * dx - 2, -table.getLastRowOffset() * gridSize - 1);
    }
    documentImage.endDraw();

    println("\nDataTable list built.");
  }

  if (e instanceof MetaTableListBuiltEvent) {
    documentImage.beginDraw();
    documentImage.noStroke();
    documentImage.fill(color(255, 128, 0), 128);
    for (MetaTable table : ((MetaTableListBuiltEvent) e).getMetaTables()) {
      documentImage.rect(table.getFirstColumn() * dx + 1, table.getFirstRow() * gridSize + 1, table.getNumberOfColumns() * dx - 2, table.getNumberOfRows() * gridSize - 1);
    }
    documentImage.endDraw();

    println("MetaTable list built.");
  }

  if (e instanceof TableGraphBuiltEvent) {
    println("TableGraph generated.");
    ((TableGraphBuiltEvent) e).dumpTableGraph();
  }
}

void displayHUD() {
  fill(0);
  stroke(255);
  rect(0, height - 17, width - 1, 16);

  fill(255);
  text("F3: Open a document", 4, height - 4);

  fill(255, 0, 0);
  int x = floor(mouseX * classifier.getLayoutClassifier().get().getSampleCount() / width);
  int y = floor((mouseY + documentTopY) / gridSize);
  String s = String.format("(%d, %d)", x, y);
  text(s, width - textWidth(s) - 4, height - 4);
}
