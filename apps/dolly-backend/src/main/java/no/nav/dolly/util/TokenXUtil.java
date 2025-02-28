package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.securitycore.domain.UserInfo;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
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

    public static UserInfo getBankidUser(GetUserInfo getUserInfo) {

        try {
            return getUserInfo.call()
                    .orElse(null);
        } catch (NullPointerException e) {
            log.info("Fant ikke BankID brukerinfo");
            return null;
        }
    }

}
