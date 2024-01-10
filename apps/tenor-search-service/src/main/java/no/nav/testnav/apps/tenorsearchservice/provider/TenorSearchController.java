package no.nav.testnav.apps.tenorsearchservice.provider;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.consumers.MaskinportenClient;
import no.nav.testnav.apps.tenorsearchservice.domain.AccessToken;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import no.nav.testnav.apps.tenorsearchservice.service.TenorSearchService;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/tenor")
@RequiredArgsConstructor
public class TenorSearchController {

    private final TenorSearchService tenorSearchService;
    private final MaskinportenClient maskinportenClient;

    @GetMapping("/testdata/raw")
    public Mono<TenorResponse> getTestdata(@RequestParam(required = false) String searchData) {

        return tenorSearchService
                .getTestdata(searchData)
                .switchIfEmpty(Mono.error(new NotFoundException("Spørring " + searchData + " ga ingen treff")));
    }

    @PostMapping("/testdata")
    public Mono<TenorResponse> getTestdata(@RequestBody TenorRequest searchData) {

        return tenorSearchService
                .getTestdata(searchData)
                .switchIfEmpty(Mono.error(new NotFoundException("Spørring " + searchData + " ga ingen treff")));
    }

    @GetMapping("/testdata/token")
    public Mono<String> getToken() {

        return maskinportenClient.getAccessToken()
                .map(AccessToken::toString);
    }

    @Bean
    public RouterFunction<ServerResponse> notFound() {
        return RouterFunctions
                .route(RequestPredicates.GET("/statuses/not-found"),
                        request -> ServerResponse.notFound().build());
    }
}