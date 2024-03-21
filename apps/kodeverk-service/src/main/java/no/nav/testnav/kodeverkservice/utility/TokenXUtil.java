package no.nav.testnav.kodeverkservice.utility;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static java.util.Objects.isNull;

@UtilityClass
@Slf4j
public final class TokenXUtil {

    public static String getUserJwt() {

        var requestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (isNull(requestAttributes)) {
            return null;
        }
        return requestAttributes.getRequest().getHeader(UserConstant.USER_HEADER_JWT);
    }
}
