package no.nav.dolly.proxy.auth;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureNavTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationFilterService {

    private final AzureNavTokenService navTokenService;
    private final AzureTrygdeetatenTokenService trygdeetatenTokenService;
    private final FakedingsService fakedingsService;
    private final TokenCache tokenCache;

    public GatewayFilter getNavAuthenticationFilter(String cluster, String namespace, String name, String url) {
        var serverProperties = ServerProperties.of(cluster, namespace, name, url);
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> tokenCache.get(serverProperties, navTokenService::exchange));
    }

    public GatewayFilter getTrygdeetatenAuthenticationFilter(String cluster, String namespace, String name, String url) {
        var serverProperties = ServerProperties.of(cluster, namespace, name, url);
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> tokenCache.get(serverProperties, trygdeetatenTokenService::exchange));
    }

    public GatewayFilter getFakedingsAuthenticationFilter(String cluster, String namespace, String name, String url) {
        var serverProperties = ServerProperties.of(cluster, namespace, name, url);
        return (exchange, chain) -> {
            var ident = exchange.getRequest().getHeaders().getFirst("fnr");
            if (!StringUtils.hasText(ident)) {
                return Mono.error(new NullPointerException("Required header 'fnr' is empty; cannot request Fakedings token with correct pid."));
            }
            var key = ident + "-" + serverProperties;
            return tokenCache
                    .get(key, () -> fakedingsService.exchange(ident, serverProperties))
                    .flatMap(token -> {
                        var mutatedExchange = exchange
                                .mutate()
                                .request(builder -> builder
                                        .headers(headers -> headers.setBearerAuth(token)))
                                .build();
                        return chain.filter(mutatedExchange);
                    });
        };
    }

    public GatewayFilter getApiKeyAuthenticationFilter(String apiKey) {
        return AddAuthenticationRequestGatewayFilterFactory
                .apiKeyAuthenticationHeaderFilter(apiKey);
    }

}
