package ec.edu.ups.content;

import ec.edu.ups.content.ContentDtos.ContentProviderResponse;
import ec.edu.ups.content.ContentDtos.ContentSearchResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ContentController {

    private final ContentService service;

    public ContentController(ContentService service) {
        this.service = service;
    }

    @GetMapping("/content/search")
    public ContentSearchResponse search(@RequestParam String topic) {
        return service.search(topic);
    }

    @GetMapping("/content/nasa")
    public ContentProviderResponse nasa(@RequestParam String topic) {
        return service.nasa(topic);
    }

    @GetMapping("/content/nytimes")
    public ContentProviderResponse nytimes(@RequestParam String topic) {
        return service.nytimes(topic);
    }
}
