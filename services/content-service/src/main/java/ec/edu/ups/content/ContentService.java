package ec.edu.ups.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.ups.content.ContentDtos.ContentProviderResponse;
import ec.edu.ups.content.ContentDtos.ContentSearchResponse;
import ec.edu.ups.content.ContentDtos.ResourceItem;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ContentService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String nasaApiKey;
    private final String nytimesApiKey;

    public ContentService(
            RestClient restClient,
            ObjectMapper objectMapper,
            @Value("${NASA_API_KEY:}") String nasaApiKey,
            @Value("${NYTIMES_API_KEY:}") String nytimesApiKey
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.nasaApiKey = nasaApiKey == null ? "" : nasaApiKey.trim();
        this.nytimesApiKey = nytimesApiKey == null ? "" : nytimesApiKey.trim();
    }

    public ContentSearchResponse search(String topic) {
        ContentProviderResponse nasa = nasa(topic);
        ContentProviderResponse nytimes = nytimes(topic);

        List<String> warnings = new ArrayList<>();
        warnings.addAll(nasa.warnings());
        warnings.addAll(nytimes.warnings());

        return new ContentSearchResponse(
                normalizeTopic(topic),
                nasa.items(),
                nytimes.items(),
                List.of("NASA API", "NYTimes API"),
                nasa.fallbackMode() || nytimes.fallbackMode(),
                warnings
        );
    }

    public ContentProviderResponse nasa(String topic) {
        String normalizedTopic = normalizeTopic(topic);
        if (nasaApiKey.isBlank()) {
            return new ContentProviderResponse(
                    normalizedTopic,
                    "NASA API",
                    List.of(fallbackNasa(normalizedTopic, "NASA_API_KEY no configurada. Se usa recurso demo.")),
                    true,
                    List.of("NASA_API_KEY no configurada")
            );
        }

        try {
            String body = restClient.get()
                    .uri("https://api.nasa.gov/planetary/apod?api_key={apiKey}", nasaApiKey)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(body);
            ResourceItem item = new ResourceItem(
                    "NASA API",
                    text(root, "title", "Imagen astronomica de NASA"),
                    text(root, "explanation", "Recurso visual de astronomia para complementar el tema " + normalizedTopic),
                    text(root, "url", "https://www.nasa.gov/"),
                    false,
                    "Recurso obtenido desde NASA APOD"
            );

            return new ContentProviderResponse(normalizedTopic, "NASA API", List.of(item), false, List.of());
        } catch (Exception ex) {
            return new ContentProviderResponse(
                    normalizedTopic,
                    "NASA API",
                    List.of(fallbackNasa(normalizedTopic, "NASA no respondio o no hay internet. Se usa recurso demo.")),
                    true,
                    List.of("NASA API fallo: " + ex.getClass().getSimpleName())
            );
        }
    }

    public ContentProviderResponse nytimes(String topic) {
        String normalizedTopic = normalizeTopic(topic);
        if (nytimesApiKey.isBlank()) {
            return new ContentProviderResponse(
                    normalizedTopic,
                    "NYTimes API",
                    List.of(fallbackNytimes(normalizedTopic, "NYTIMES_API_KEY no configurada. Se usa articulo demo.")),
                    true,
                    List.of("NYTIMES_API_KEY no configurada")
            );
        }

        try {
            String body = restClient.get()
                    .uri("https://api.nytimes.com/svc/search/v2/articlesearch.json?q={topic}&api-key={apiKey}", normalizedTopic, nytimesApiKey)
                    .retrieve()
                    .body(String.class);

            JsonNode docs = objectMapper.readTree(body).path("response").path("docs");
            List<ResourceItem> articles = new ArrayList<>();
            if (docs.isArray()) {
                for (JsonNode doc : docs) {
                    if (articles.size() >= 3) {
                        break;
                    }
                    articles.add(new ResourceItem(
                            "NYTimes API",
                            text(doc.path("headline"), "main", "Articulo relacionado con " + normalizedTopic),
                            text(doc, "abstract", "Articulo de referencia para contextualizar el tema academico."),
                            text(doc, "web_url", "https://www.nytimes.com/"),
                            false,
                            "Articulo obtenido desde NYTimes Article Search"
                    ));
                }
            }

            if (articles.isEmpty()) {
                articles.add(fallbackNytimes(normalizedTopic, "NYTimes no devolvio resultados. Se usa articulo demo."));
                return new ContentProviderResponse(normalizedTopic, "NYTimes API", articles, true, List.of("NYTimes sin resultados"));
            }

            return new ContentProviderResponse(normalizedTopic, "NYTimes API", articles, false, List.of());
        } catch (Exception ex) {
            return new ContentProviderResponse(
                    normalizedTopic,
                    "NYTimes API",
                    List.of(fallbackNytimes(normalizedTopic, "NYTimes no respondio o no hay internet. Se usa articulo demo.")),
                    true,
                    List.of("NYTimes API fallo: " + ex.getClass().getSimpleName())
            );
        }
    }

    private ResourceItem fallbackNasa(String topic, String message) {
        return new ResourceItem(
                "NASA API",
                "Recurso demo NASA para " + topic,
                "Contenido de respaldo para demostrar la integracion con proveedor externo cuando no hay clave API o conectividad.",
                "https://www.nasa.gov/",
                true,
                message
        );
    }

    private ResourceItem fallbackNytimes(String topic, String message) {
        return new ResourceItem(
                "NYTimes API",
                "Articulo demo NYTimes sobre " + topic,
                "Contenido de respaldo para demostrar la integracion con proveedor externo cuando no hay clave API o conectividad.",
                "https://www.nytimes.com/",
                true,
                message
        );
    }

    private String normalizeTopic(String topic) {
        if (topic == null || topic.isBlank()) {
            return "tema academico";
        }
        return topic.trim();
    }

    private String text(JsonNode node, String field, String fallback) {
        JsonNode value = node.path(field);
        return value.isTextual() && !value.asText().isBlank() ? value.asText() : fallback;
    }
}
