package no.nav.registre.testnorge.libs.logging;

import javax.servlet.http.HttpServletRequest;

public class LogApiRequestInterceptor extends LogRequestInterceptor {
    @Override
    boolean shouldLogRequest(HttpServletRequest request) {
        return request.getRequestURI().matches("^\\/api\\/.*$");
    }
}
