package no.nav.testnav.libs.servletcore.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class LogRequestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (log.isTraceEnabled() && shouldLogRequest(request)) {
            try {

                var method = String.valueOf(request.getMethod());
                var host = request.getHeader(HttpHeaders.HOST);
                var uri = request.getRequestURI();

                Map<String, String> contextMap = MDC.getCopyOfContextMap() != null ? MDC.getCopyOfContextMap() : new HashMap<>();
                contextMap.put("Transaction-Type", "request");

                contextMap.put("Method", method);
                contextMap.put(HttpHeaders.CONTENT_TYPE, request.getContentType());
                contextMap.put(HttpHeaders.HOST, host);
                contextMap.put(HttpHeaders.ORIGIN, request.getHeader(HttpHeaders.ORIGIN));
                contextMap.put(HttpHeaders.REFERER, request.getHeader(HttpHeaders.REFERER));
                contextMap.put(HttpHeaders.ACCEPT, request.getHeader(HttpHeaders.ACCEPT));
                contextMap.put("URI", uri);

                MDC.setContextMap(contextMap);
                log.trace("[Request ] {} {}{}", method, host, uri);
            } catch (Exception e) {
                log.warn("Feil med logging av requests", e);
            }
        }
        return true;
    }

    abstract boolean shouldLogRequest(HttpServletRequest request);
}
