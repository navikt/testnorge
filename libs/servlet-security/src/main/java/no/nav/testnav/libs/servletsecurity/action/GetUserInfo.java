package no.nav.testnav.libs.servletsecurity.action;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.concurrent.Callable;

import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.securitycore.domain.UserInfo;

@Slf4j
@Component
public class GetUserInfo implements Callable<Optional<UserInfo>> {

    private final String secret;

    public GetUserInfo(@Value("${JWT_SECRET:#{null}}") String secret) {
        this.secret = secret;
    }

    @Override
    public Optional<UserInfo> call() {
        var request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        log.info("Prøver å hente JWT fra request...");
        return Optional.ofNullable(request.getHeader(UserConstant.USER_HEADER_JWT)).map(token -> {
            log.info("Fant JWT i request.");
            var jwt = JWT.decode(token);
            var verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            verifier.verify(jwt);
            return new UserInfo(
                    jwt.getClaim(UserConstant.USER_CLAIM_ID).asString(),
                    jwt.getClaim(UserConstant.USER_CLAIM_ORG).asString(),
                    jwt.getIssuer(),
                    jwt.getClaim(UserConstant.USER_CLAIM_USERNAME).asString()
            );
        });
    }
}
