enum EntityType {
  NONE, 
  DATE, 
  CODEPOSTAL, 
  REFERENCE, 
  DOSAGE, 
  SMALL,
  NUMBER
}

enum Tag {
  NONE,
  AMOUNT,
  QUANTITY
}

class Entity {
  String pattern;
  EntityType type;

  Entity(String pattern, EntityType type) {
    this.pattern = pattern;
    this.type = type;
  }
}

class Extractor {
  String[] stopwords; 
  Entity[] entities;

  void loadStopWords() {
    processing.data.Table table = loadTable("data/stopwords.txt", "csv");

    this.stopwords = new String[table.getRowCount()];

    int i = 0;
    for (TableRow row : table.rows()) {
      this.stopwords[i] = row.getString(0).trim();
      i++;
    }
  }
  
  void loadEntities() {
    processing.data.Table table = loadTable("data/entities.txt", "csv");

    this.entities = new Entity[table.getRowCount()];

    int i = 0;
    for (TableRow row : table.rows()) {
      this.entities[i] = new Entity(row.getString(0).trim(), EntityType.valueOf(row.getString(1).trim()));
      i++;
    }
  }
  
  String removeStopWords(String s) {
    for (int i  = 0; i < this.stopwords.length; i++) {
      s = s.replaceAll(this.stopwords[i], "");
    } 
    return s;
  }

  EntityType[] findEntityTypes(String s) {
    EntityType[] result = new EntityType[this.entities.length];

    for (int i  = 0; i < this.entities.length; i++) {
      String[] m = match(s, this.entities[i].pattern);
      if (m != null) {
        result[i] = this.entities[i].type;
      } else {
        result[i] = EntityType.NONE;
      }
    }

    return result;
  }
}
