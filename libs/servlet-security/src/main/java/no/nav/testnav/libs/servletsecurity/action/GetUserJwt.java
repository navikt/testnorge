package no.nav.testnav.libs.servletsecurity.action;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.concurrent.Callable;

import no.nav.testnav.libs.securitycore.config.UserConstant;

@Component
public class GetUserJwt implements Callable<Optional<String>> {

    @Override
    public Optional<String> call() {
        var request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return Optional.ofNullable(request.getHeader(UserConstant.USER_HEADER_JWT));
    }
}
