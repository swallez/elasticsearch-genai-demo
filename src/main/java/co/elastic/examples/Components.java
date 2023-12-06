package co.elastic.examples;

import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingLanguageModel;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

public class Components {

    private static final Map<String, String> config;
    static {
        try (var input = new FileInputStream("config.properties")) {
            var props = new Properties();
            props.load(input);
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) (Map<?, ?>) props;
            config = map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static LineReader createLineReader() throws IOException {
        Terminal terminal = TerminalBuilder.builder().dumb(true).build();
        return LineReaderBuilder.builder().terminal(terminal).build();
    }

    public static OllamaEmbeddingModel createEmbeddingModel() {
        return new OllamaEmbeddingModel(
            config.get("ollama-url"),
            Duration.ofSeconds(30),
            config.get("ollama-embed-model"),
            1
        );
    }

    public static OllamaStreamingLanguageModel createLanguageModel() {
        return new OllamaStreamingLanguageModel(
            config.get("ollama-url"),
            Duration.ofSeconds(30),
            config.get("ollama-language-model"),
            null
        );
    }

    public static ElasticsearchEmbeddingStore createStore(String index) {
        var config = readConfig();
        return ElasticsearchEmbeddingStore.builder()
            .serverUrl(config.get("es-url"))
            .userName(config.get("es-login"))
            .password(config.get("es-password"))
            .dimension(Integer.parseInt(config.get("ollama-embed-dimensions")))
            .indexName(index)
            .build();
    }

    public static Map<String, String> readConfig() {
        try (var input = new FileInputStream("config.properties")) {
            var props = new Properties();
            props.load(input);
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) (Map<?, ?>) props;
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
