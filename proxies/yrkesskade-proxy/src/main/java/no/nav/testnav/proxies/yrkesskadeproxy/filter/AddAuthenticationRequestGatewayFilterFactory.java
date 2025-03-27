package no.nav.testnav.proxies.yrkesskadeproxy.filter;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.proxies.yrkesskadeproxy.consumer.FakedingsConsumer;
import org.springframework.cloud.gateway.filter.GatewayFilter;

@Slf4j
@UtilityClass
public class AddAuthenticationRequestGatewayFilterFactory {
    public static GatewayFilter bearerIdportenHeaderFilter(FakedingsConsumer fakedingsConsumer,
                                                           TokenXService tokenXService,
                                                           ServerProperties serverProperties) {

        return (exchange, chain) -> {
            var httpRequest = exchange.getRequest();
            var ident = httpRequest.getHeaders().getFirst("ident");

            return fakedingsConsumer.getFakeToken(ident)
                    .flatMap(faketoken -> tokenXService.exchange(serverProperties, faketoken)
                            .flatMap(tokenX -> {
                                var mutatedExchange = exchange
                                        .mutate()
                                        .request(builder -> builder
                                                .headers(WebClientHeader.bearer(tokenX.getTokenValue()))
                                                .build())
                                        .build();
                                return chain.filter(mutatedExchange);
                            }));
        };
    }
}