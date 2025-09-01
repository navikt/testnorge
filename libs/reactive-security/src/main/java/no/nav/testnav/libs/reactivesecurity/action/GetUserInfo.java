package no.nav.testnav.libs.reactivesecurity.action;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static java.util.Collections.emptyList;

@Slf4j
@Component
public class GetUserInfo extends JwtResolver implements Callable<Mono<UserInfoExtended>> {

    @Override
    public Mono<UserInfoExtended> call() {

        return getJwtAuthenticationToken()
                .map(authentication -> {

                    if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {

                        var attrib = jwtAuthenticationToken.getTokenAttributes();

                        return new UserInfoExtended(
                                (String) attrib.get("oid"),
                                "889640782",
                                (String) attrib.get("iss"),
                                (String) attrib.get("name"),
                                (String) attrib.get("preferred_username"),
                                false,
                                (List<String>) attrib.get("groups"));

                    } else if (authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken) {

                        var attrib = oauth2AuthenticationToken.getPrincipal().getAttributes();
                        return new UserInfoExtended(
                                (String) attrib.get("pid"),
                                (String) attrib.get(UserConstant.USER_CLAIM_ORG),
                                (String) attrib.get("issuer"),
                                (String) attrib.get(UserConstant.USER_CLAIM_USERNAME),
                                "",
                                true,
                                emptyList());
                    } else {

                        return new UserInfoExtended(null, null, null,
                                null, null, false, emptyList());
                    }
                });
    }
}
