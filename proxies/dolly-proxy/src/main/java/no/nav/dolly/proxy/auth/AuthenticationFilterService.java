package no.nav.dolly.proxy.auth;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureNavTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationFilterService {

    private final AzureNavTokenService navTokenService;
    private final AzureTrygdeetatenTokenService trygdeetatenTokenService;
    private final FakedingsService fakedingsService;
    private final TokenCache tokenCache;

    public GatewayFilter getNavAuthenticationFilter(ServerProperties serverProperties) {
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> tokenCache.get(serverProperties, navTokenService::exchange));
    }

    public GatewayFilter getTrygdeetatenAuthenticationFilter(ServerProperties serverProperties) {
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> tokenCache.get(serverProperties, trygdeetatenTokenService::exchange));
    }

    public GatewayFilter getFakedingsAuthenticationFilter(ServerProperties serverProperties) {
        return fakedingsService
                .bearerAuthenticationHeaderFilter(serverProperties);
    }

    public GatewayFilter getApiKeyAuthenticationFilter(String apiKey) {
        return AddAuthenticationRequestGatewayFilterFactory
                .apiKeyAuthenticationHeaderFilter(apiKey);
    }

}
