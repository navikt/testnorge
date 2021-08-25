package no.nav.registre.aareg.config.provider;

import no.nav.registre.aareg.exception.MissingHttpHeaderException;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static no.nav.registre.aareg.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.registre.aareg.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static no.nav.registre.aareg.domain.CommonKeys.MDC_CALL_ID_KEY;
import static no.nav.registre.aareg.domain.CommonKeys.MDC_CONSUMER_ID_KEY;

@Component
public class RestMdcInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String callId = extractAndValidateHeaderValue(request, HEADER_NAV_CALL_ID);
        String consumerId = extractAndValidateHeaderValue(request, HEADER_NAV_CONSUMER_ID);

        MDC.put(MDC_CALL_ID_KEY, callId);
        MDC.put(MDC_CONSUMER_ID_KEY, consumerId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(MDC_CONSUMER_ID_KEY);
        MDC.remove(MDC_CALL_ID_KEY);
    }

    private String extractAndValidateHeaderValue(HttpServletRequest request, String headerName) {
        return Optional.ofNullable(request.getHeader(headerName))
                .filter(headerValue -> !headerValue.isEmpty())
                .orElseThrow(() -> new MissingHttpHeaderException(headerName));
    }
}
