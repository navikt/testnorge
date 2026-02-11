package no.nav.dolly.proxy.auth;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureNavTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationFilterService {

    private final AzureNavTokenService navTokenService;
    private final AzureTrygdeetatenTokenService trygdeetatenTokenService;
    private final FakedingsService fakedingsService;

    public GatewayFilter getNavAuthenticationFilter(String cluster, String namespace, String name, String url) {
        var serverProperties = ServerProperties.of(cluster, namespace, name, url);
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> navTokenService
                                .exchange(serverProperties)
                                .map(AccessToken::getTokenValue));
    }

    public GatewayFilter getTrygdeetatenAuthenticationFilter(String cluster, String namespace, String name, String url) {
        var serverProperties = ServerProperties.of(cluster, namespace, name, url);
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> trygdeetatenTokenService
                                .exchange(serverProperties)
                                .map(AccessToken::getTokenValue));
    }

    public GatewayFilter getFakedingsAuthenticationFilter(String cluster, String namespace, String name, String url) {
        var serverProperties = ServerProperties.of(cluster, namespace, name, url);
        return fakedingsService
                .bearerAuthenticationHeaderFilter(serverProperties);
    }

    public GatewayFilter getApiKeyAuthenticationFilter(String apiKey) {
        return AddAuthenticationRequestGatewayFilterFactory
                .apiKeyAuthenticationHeaderFilter(apiKey);
    }

}
