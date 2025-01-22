package no.nav.testnav.proxies.fullmaktproxy.filter;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.proxies.fullmaktproxy.consumer.FakedingsConsumer;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpHeaders;

import static java.util.Objects.nonNull;

@Slf4j
@UtilityClass
public class AddAuthenticationRequestGatewayFilterFactory {
    public static GatewayFilter bearerIdportenHeaderFilter(FakedingsConsumer fakedingsConsumer,
                                                           TokenXService tokenXService,
                                                           ServerProperties serverProperties) {

        return (exchange, chain) -> {
            var httpRequest = exchange.getRequest();
            var ident = httpRequest.getHeaders().getFirst("fnr");

            return fakedingsConsumer.getFakeToken(ident)
                    .flatMap(faketoken -> tokenXService.exchange(serverProperties, faketoken)
                            .flatMap(tokenX -> {
                                exchange.mutate()
                                        .request(builder -> builder.header(HttpHeaders.AUTHORIZATION,
                                                "Bearer " + tokenX.getTokenValue()).build());

                                // Log the outgoing request details
                                var modifiedRequest = exchange.getRequest();
                                var bearer = modifiedRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                                log.info("Outgoing request: method={}, uri={}, headers={}",
                                        modifiedRequest.getMethod(), modifiedRequest.getURI(), modifiedRequest.getHeaders());
                                if (nonNull(bearer)) {
                                    log.info("Fakedings tokenx auth header: {}", bearer.replaceAll("Bearer ", ""));
                                }

                                return chain.filter(exchange);
                            }));
        };
    }
}