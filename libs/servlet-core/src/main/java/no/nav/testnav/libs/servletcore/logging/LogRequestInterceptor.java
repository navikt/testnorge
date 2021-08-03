package no.nav.testnav.libs.servletcore.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class LogRequestInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (log.isTraceEnabled() && shouldLogRequest(request)) {
            try {
                Map<String, String> contextMap = MDC.getCopyOfContextMap() != null ? MDC.getCopyOfContextMap() : new HashMap<>();
                contextMap.put("Transaction-Type", "request");
                contextMap.put("Method", String.valueOf(request.getMethod()));
                contextMap.put(HttpHeaders.CONTENT_TYPE, request.getContentType());
                contextMap.put(HttpHeaders.HOST, request.getHeader(HttpHeaders.HOST));
                contextMap.put(HttpHeaders.ORIGIN, request.getHeader(HttpHeaders.ORIGIN));
                contextMap.put(HttpHeaders.REFERER, request.getHeader(HttpHeaders.REFERER));
                contextMap.put("URI", request.getRequestURI());
                MDC.setContextMap(contextMap);
                log.trace("[Registered request]");
            } catch (Exception e) {
                log.warn("Feil med logging av requests", e);
            }
        }
        return super.preHandle(request, response, handler);
    }

    abstract boolean shouldLogRequest(HttpServletRequest request);
}
