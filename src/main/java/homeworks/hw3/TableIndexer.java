package homeworks.hw3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TableIndexer {
    public static void main(String[] args) throws IOException {
        String filePath = "C:\\Users\\Fero\\Desktop\\hw3\\tables.json";
        FileReader fileReader = new FileReader(filePath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
        perFieldAnalyzers.put("tabella_colonna", new StandardAnalyzer());
        perFieldAnalyzers.put("contenuto", new StandardAnalyzer());
        Analyzer analyzer = new PerFieldAnalyzerWrapper(new EnglishAnalyzer(), perFieldAnalyzers);
        Path path = Paths.get("C:\\Users\\Fero\\Desktop\\hw3\\indici");
        Directory directory = FSDirectory.open(path);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setCodec(new SimpleTextCodec());
        IndexWriter writer = new IndexWriter(directory, config);
        writer.deleteAll();

        long startTime = System.currentTimeMillis();
        String line;
        
        
        int tableCount = 0;
        final int maxTables = 550271; 
        
        while (tableCount < maxTables && (line = bufferedReader.readLine()) != null) {
            JsonElement jsonTree = JsonParser.parseString(line);
            JsonObject table = jsonTree.getAsJsonObject();
            Document doc = new Document();
            
            
            Map<String, String> colonnaToCella = new HashMap<>();

            JsonArray cells = table.getAsJsonArray("cells");
            int cellsNumber = cells.size();
            for (int j = 0; j < cellsNumber; j++) {
                JsonObject cellObject = cells.get(j).getAsJsonObject();
              
                if (!cellObject.get("isHeader").getAsBoolean()){
                    JsonObject coordinates = cellObject.get("Coordinates").getAsJsonObject();
                    String column = coordinates.get("column").getAsString();
                    String cell = cellObject.get("cleanedText").getAsString();
                    if (!cell.equals("")) {
                        if (colonnaToCella.containsKey(column)) {
                            colonnaToCella.put(column, colonnaToCella.get(column) + "~" + cell);
                        } else {
                            colonnaToCella.put(column, cell);
                        }
                    }
                }
            }
            
            if (!colonnaToCella.isEmpty()) {
                for (Map.Entry<String, String> entry : colonnaToCella.entrySet()) {
                    String col = entry.getKey();
                    String cella = entry.getValue();
                    doc.add(new TextField("set", "X(" + Integer.toString(tableCount) + ";" + col + ")", Field.Store.YES));
                    doc.add(new TextField("contenuto", cella, Field.Store.YES));
                }
                writer.addDocument(doc);
                System.out.println("Documento aggiunto #: " + tableCount);
            }
            
            tableCount++; 
        }

        writer.commit();
        writer.close();
        bufferedReader.close();
        fileReader.close();

        long endTime = System.currentTimeMillis();
        System.out.println("Tempo di indicizzazione per " +maxTables+ " tabelle: " + (endTime - startTime) / 1000 + " secondi");
    }
}
