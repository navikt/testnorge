package no.nav.testnav.proxies.modapperegproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        var uris = exchange.getAttributeOrDefault(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());
        var originalUri = (uris.isEmpty()) ? "Unknown" : uris.iterator().next().toString();
        var route = (Route) exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        var routeUri = (URI) exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        log.info("Incoming request {} is routed to id: {}, uri: {}", originalUri , route.getId(), routeUri);
        return chain.filter(exchange);
    }
}