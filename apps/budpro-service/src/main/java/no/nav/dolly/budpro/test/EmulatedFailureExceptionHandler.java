package no.nav.dolly.budpro.test;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-1)
class EmulatedFailureExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof EmulatedFailureException e) {
            exchange.getResponse().setStatusCode(e.getStatus());
            return exchange.getResponse().setComplete();
        } else {
            return Mono.error(ex);
        }
    }

}
