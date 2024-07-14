package homeworks.hw3;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Paths;
import java.util.*;

public class QueryProcessor {

    public static void main(String[] args) throws Exception {
        // Imposta il percorso dell'indice Lucene
        String indexPath = "C:\\Users\\Fero\\Desktop\\Ingegneria dei Dati\\hw3\\indici";
        
        // Apri una DirectoryReader per leggere l'indice
        Directory directory = FSDirectory.open(Paths.get(indexPath));
        IndexReader reader = DirectoryReader.open(directory);

        // Inizializza l'analyzer
        StandardAnalyzer analyzer = new StandardAnalyzer();
        
        // Definisci un query parser per il campo di ricerca
        QueryParser parser = new QueryParser("contenuto", analyzer);
        
        // La tua lista di token di query
        String[] queryTokens = {"singular", "dual" , "rome" , "milan" , "module", "test" , "bread" , "tree"};
        
        // Inizializza la mappa per mantenere i conteggi dei set
        HashMap<String, Integer> set2count = new HashMap<>();
        
        // Per ogni token di query
        for (String token : queryTokens) {
            // Crea una query
            Query query = parser.parse(token);

            // Cerca nel reader
            ScoreDoc[] hits = new IndexSearcher(reader).search(query, reader.maxDoc()).scoreDocs;
            
            // Per ogni hit incrementa il contatore per il candidato (documento)
            for (ScoreDoc hit : hits) {
                Document doc = reader.document(hit.doc);
                String set = doc.get("set");
                
                // Se il set è già presente, incrementa il contatore, altrimenti inizializzalo a 1
                set2count.put(set, set2count.getOrDefault(set, 0) + 1);
            }
        }
        
        // Ordina i set in base al loro contatore di intersezione
        List<Map.Entry<String, Integer>> list = new ArrayList<>(set2count.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Prendi i k set con il contatore più alto
        int k = 10; // Numero di set da restituire
        List<Map.Entry<String, Integer>> topKSets = list.subList(0, Math.min(k, list.size()));

        // Stampa i set con il contatore più alto
        for (Map.Entry<String, Integer> entry : topKSets) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
        
        // Chiudi le risorse
        reader.close();
        directory.close();
    }
}
