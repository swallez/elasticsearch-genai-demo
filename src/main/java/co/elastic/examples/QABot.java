package co.elastic.examples;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.language.StreamingLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.jline.reader.LineReader;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class QABot {

    public static void main(String[] args) throws Exception {

        EmbeddingStore<TextSegment> store = Components.createStore(WorkplaceDoc.INDEX);
        EmbeddingModel embedModel = Components.createEmbeddingModel();
        StreamingLanguageModel languageModel = Components.createLanguageModel();

        LineReader reader = Components.createLineReader();
        while(true) {

            var question = reader.readLine("QA bot> ");
            if (question.equals("/bye")) {
                break;
            }

            if (question.isEmpty()) {
                continue;
            }

            // Convert question text to embeddings
            Embedding questionEmbedding = embedModel.embed(question).content();

            // Find similar documents (vector search)
            List<EmbeddingMatch<TextSegment>> relevant = store.findRelevant(
                questionEmbedding, // question embedding
                4,    // max results
                0.0   // min score
            );

            // Context: concatenation of results text fragments
            String context = relevant.stream().map(r -> r.embedded().text()).collect(Collectors.joining("\n\n"));

            String prompt = context + "\n\n" + question;

            var handler = new ResponseHandler();

            new Thread(() -> languageModel.generate(prompt, handler)).run();
            handler.waitForCompletion();

            // Print sources that were used to build the response
            System.out.println("---------------------------");
            System.out.println("Sources:");
            for (var x: relevant) {
                var emb = x.embedded();
                System.out.println(x.score() + " Title: " + emb.metadata().get("title"));
            }

            System.out.println();
        };

        System.out.println("Happy to help, bye!");
        System.exit(0);
    }

    static class ResponseHandler implements StreamingResponseHandler<String> {
        private final CompletableFuture<Void> future = new CompletableFuture<Void>();

        @Override
        public void onNext(String token) {
            System.out.print(token);
            System.out.flush();
        }

        @Override
        public void onComplete(Response<String> response) {
            System.out.println();
            future.complete(null);
        }

        @Override
        public void onError(Throwable error) {
            onComplete(null);
        }

        public void waitForCompletion() throws Exception {
            future.get();
        }
    }
}
