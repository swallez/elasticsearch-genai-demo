package co.elastic.examples;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;

import java.util.List;

public class Indexer {

    public static void main(String[] args) throws Exception {

        String index = WorkplaceDoc.INDEX;
        var docs = WorkplaceDoc.load();

        ElasticsearchEmbeddingStore store = Components.createStore(index);

        EmbeddingModel embedModel = Components.createEmbeddingModel();

        // Delete existing data
        store.getElasticsearchClient().indices().delete(d -> d.index(index).allowNoIndices(true));

        DocumentSplitter splitter = new DocumentByParagraphSplitter(1000, 50);

        int count = 0;
        for (var doc: docs) {
            count++;

            // Split the document in segments
            List<TextSegment> segments = splitter.split(doc);

            System.err.print(count + "/" + docs.size() + " - " + doc.metadata("title") + ". " + segments.size() + " segments. Extracting embeddings. ");

            // Extract embeddings from all segments
            List<Embedding> embeddings = embedModel
                .embedAll(segments)
                .content();

            System.err.print("Done. Storing. ");

            // Store vector embeddings in Elasticsearch
            store.addAll(embeddings, segments);

            System.err.println("Done.");
        }

        System.out.println(count + " documents done.");
        System.exit(0);
    }

/*
    Indexing output:

    1/15 - Work From Home Policy. 4 segments. Extracting embeddings. Done. Storing. Done.
    2/15 - April Work From Home Update. 1 segments. Extracting embeddings. Done. Storing. Done.
    3/15 - Wfh Policy Update May 2023. 1 segments. Extracting embeddings. Done. Storing. Done.
    4/15 - Fy2024 Company Sales Strategy. 4 segments. Extracting embeddings. Done. Storing. Done.
    5/15 - Company Vacation Policy. 4 segments. Extracting embeddings. Done. Storing. Done.
    6/15 - Swe Career Matrix. 6 segments. Extracting embeddings. Done. Storing. Done.
    7/15 - Sales Engineering Collaboration. 5 segments. Extracting embeddings. Done. Storing. Done.
    8/15 - Intellectual Property Policy. 4 segments. Extracting embeddings. Done. Storing. Done.
    9/15 - Code Of Conduct. 5 segments. Extracting embeddings. Done. Storing. Done.
    10/15 - Office Pet Policy. 4 segments. Extracting embeddings. Done. Storing. Done.
    11/15 - Performance Management Policy. 5 segments. Extracting embeddings. Done. Storing. Done.
    12/15 - Sales Organization Overview. 4 segments. Extracting embeddings. Done. Storing. Done.
    13/15 - Compensation Framework For It Teams. 5 segments. Extracting embeddings. Done. Storing. Done.
    14/15 - Updating Your Tax Elections Forms. 3 segments. Extracting embeddings. Done. Storing. Done.
    15/15 - New Employee Onboarding Guide. 6 segments. Extracting embeddings. Done. Storing. Done.
*/
}
