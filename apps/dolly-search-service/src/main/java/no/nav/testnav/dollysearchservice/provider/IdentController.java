package no.nav.testnav.dollysearchservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollysearchservice.domain.ElasticTyper;
import no.nav.testnav.dollysearchservice.model.SearchResponse;
import no.nav.testnav.dollysearchservice.service.IdentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/elastic")
public class IdentController {

    private final IdentService identService;

    @GetMapping("/typer")
    public SearchResponse getIdenter(@RequestParam ElasticTyper[] typer) {

        return identService.getIdenter(typer);
    }
}
