package ec.edu.ups.content;

import java.util.List;

public final class ContentDtos {

    private ContentDtos() {
    }

    public record ResourceItem(
            String provider,
            String title,
            String description,
            String url,
            boolean fallback,
            String message
    ) {
    }

    public record ContentProviderResponse(
            String topic,
            String provider,
            List<ResourceItem> items,
            boolean fallbackMode,
            List<String> warnings
    ) {
    }

    public record ContentSearchResponse(
            String topic,
            List<ResourceItem> nasaResources,
            List<ResourceItem> nytimesArticles,
            List<String> providersUsed,
            boolean fallbackMode,
            List<String> warnings
    ) {
    }
}
