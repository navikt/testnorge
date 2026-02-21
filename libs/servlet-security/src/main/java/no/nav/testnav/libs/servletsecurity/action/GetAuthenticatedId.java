package no.nav.testnav.libs.servletsecurity.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;

import no.nav.testnav.libs.securitycore.domain.UserInfo;

@Component
@RequiredArgsConstructor
public class GetAuthenticatedId extends JwtResolver implements Callable<String> {

    private final GetAuthenticatedResourceServerType getAuthenticatedResourceServerType;
    private final GetUserInfo getUserInfo;

    @Override
    public String call() {
        Map<String, Object> tokenAttributes = getJwtAuthenticationToken().getTokenAttributes();
        return switch (getAuthenticatedResourceServerType.call()) {
            case TOKEN_X -> getUserInfo.call().map(UserInfo::id).orElseThrow();
            case AZURE_AD -> tokenAttributes.get("oid") == null ? null : String.valueOf(tokenAttributes.get("oid"));
        };
    }
}
