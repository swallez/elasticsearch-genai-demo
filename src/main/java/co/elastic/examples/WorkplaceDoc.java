package co.elastic.examples;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

public record WorkplaceDoc (
    String content,
    String summary,
    String name,
    String url,
    String created_on,
    String updated_at,
    String category,
    boolean restricted,
    boolean _run_ml_inference,
    String[] rolePermissions
) {

    public static final String INDEX = "workplace-docs-embeddings";

    public Document toDocument() {
        var meta = Map.of(
            "summary", summary,
            "title", name, // <--- Standardize on "title"
            "url", url,
            "category", category
        );
        return new Document("Title: " + name + "\n\n" + content, new Metadata(meta));
    }

    public static List<Document> load() {

        try (var in = new FileInputStream("data/workplace-documents.json")) {

            var docs = new ObjectMapper().readValue(in, new TypeReference<List<WorkplaceDoc>>() {});
            return docs.stream().map(WorkplaceDoc::toDocument).toList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
