package no.nav.dolly.proxy.route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Slf4j
class NotFound {

    Function<PredicateSpec, Buildable<Route>> build() {
        return spec -> spec
                .path("/**")
                .filters(f -> f.filter((exchange, chain) -> {
                    log.info("No route found for {}", exchange.getRequest().getPath());
                    exchange
                            .getResponse()
                            .setStatusCode(HttpStatus.NOT_FOUND);
                    return exchange
                            .getResponse()
                            .setComplete();
                }))
                .uri("no://op");
    }

}
