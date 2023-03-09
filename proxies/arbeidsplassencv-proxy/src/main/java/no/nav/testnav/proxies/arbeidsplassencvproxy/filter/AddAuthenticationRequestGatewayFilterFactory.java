package no.nav.testnav.proxies.arbeidsplassencvproxy.filter;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.proxies.arbeidsplassencvproxy.consumer.FakedingsConsumer;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Slf4j
@UtilityClass
public class AddAuthenticationRequestGatewayFilterFactory {
    public static GatewayFilter bearerIdportenHeaderFilter(FakedingsConsumer fakedingsConsumer,
                                                           TokenXService tokenXService,
                                                           ServerProperties serverProperties,
                                                           Supplier<Mono<String>> getToken) {

        return (exchange, chain) -> {
            var httpRequest = exchange.getRequest();
            var ident = httpRequest.getHeaders().getFirst("fnr");
            return fakedingsConsumer.getFakeToken(ident)
                    .flatMap(faketoken -> tokenXService.exchange(serverProperties, faketoken)
                            .doOnNext(tokenX -> log.info(tokenX.getTokenValue()))
                            .flatMap(tokenX -> {
                                exchange.mutate()
                                        .request(builder -> builder.header(HttpHeaders.AUTHORIZATION,
                                                "Bearer " + tokenX.getTokenValue()).build());
                                return chain.filter(exchange);
                            }));
        };
    }
}