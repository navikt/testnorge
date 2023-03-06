package no.nav.testnav.proxies.arbeidsplassencvproxy.filter;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.proxies.arbeidsplassencvproxy.consumer.FakedingsConsumer;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@UtilityClass
public class AddAuthenticationRequestGatewayFilterFactory {
    public static GatewayFilter bearerIdportenHeaderFilter(FakedingsConsumer fakedingsConsumer,
                                                           TokenXService tokenXService,
                                                           Supplier<Mono<String>> getToken) {

        return (exchange, chain) -> {
            var httpRequest = exchange.getRequest();
            var ident = httpRequest.getHeaders().getFirst("fnr");
            return getToken.get()
                    .flatMap(token -> fakedingsConsumer.getFakeToken(ident)
                            .flatMap(faketoken -> tokenXService.exchange(faketoken)
                                    .flatMap(tokenX -> {
                                                exchange.mutate()
                                                        .request(builder -> builder.header(HttpHeaders.AUTHORIZATION,
                                                                "Bearer " + tokenX.getTokenValue()).build());
                                                return chain.filter(exchange);
                                            })));
        };
    }
}