package com.prodyna.test;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.IOException;

public class LuceneTest {

    @Test
    public void test() throws Exception {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);

        IndexWriter writer = new IndexWriter(directory, config);
        addDoc(writer, "Lucene in Action", "193398817");
        addDoc(writer, "Lucene for Dummies", "55320055Z");
        addDoc(writer, "Managing Gigabytes", "55063554A");
        addDoc(writer, "The Art of Computer Science", "9900333X");
        writer.close();

        Query query = new QueryParser(Version.LATEST, "title", analyzer).parse("lucene");

        IndexReader reader = IndexReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);
        searcher.search(query, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        for (ScoreDoc hit : hits) {
            int docId = hit.doc;
            Document doc = searcher.doc(docId);
            System.out.printf("score=%f docId=%d title=%s isbn=%s\n", hit.score, docId, doc.getField("title").stringValue(), doc.getField("isbn").stringValue());
        }
    }

    private void addDoc(IndexWriter writer, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        writer.addDocument(doc);
    }

}
