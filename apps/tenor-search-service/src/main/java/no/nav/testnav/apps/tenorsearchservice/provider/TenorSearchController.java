package no.nav.testnav.apps.tenorsearchservice.provider;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.service.TenorSearchService;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/testdata")
    public Mono<JsonNode> getTestdata(@RequestParam(required = false) String searchData) {
        return tenorSearchService
                .getTestdata(searchData)
                .switchIfEmpty(Mono.error(new NotFoundException("Spørring " + searchData + " ga ingen treff")));
    }

    @Bean
    public RouterFunction<ServerResponse> notFound() {
        return RouterFunctions
                .route(RequestPredicates.GET("/statuses/not-found"),
                        request -> ServerResponse.notFound().build());
    }
}