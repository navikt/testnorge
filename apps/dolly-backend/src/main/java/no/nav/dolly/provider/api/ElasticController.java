package no.nav.dolly.provider.api;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.elastic.ElasticTyper;
import no.nav.dolly.elastic.dto.SearchRequest;
import no.nav.dolly.elastic.dto.SearchResponse;
import no.nav.dolly.elastic.service.ElasticsearchSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/elastic")
@RequiredArgsConstructor
public class ElasticController {

    private final ElasticsearchSearchService elasticsearchSearchService;

    @GetMapping("/all")
    public List<ElasticBestilling> getAll() {

        return elasticsearchSearchService.getAll();
    }

    @GetMapping("/typer")
    public SearchResponse getIdenterMed(@RequestParam ElasticTyper... typer) {

        return elasticsearchSearchService.getTyper(typer);
    }

    @PostMapping()
    public SearchResponse getIdenterMed(@RequestBody SearchRequest request) {

        return elasticsearchSearchService.search(request);
    }
}
