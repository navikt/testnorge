package no.nav.testnav.libs.servletcore.logging;

import jakarta.servlet.http.HttpServletRequest;

public class LogApiRequestInterceptor extends LogRequestInterceptor {
    @Override
    boolean shouldLogRequest(HttpServletRequest request) {
        return request.getRequestURI().matches("^\\/api\\/.*$");
    }
}
