package no.nav.registre.testnav.inntektsmeldingservice.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@Slf4j
class RequestLoggingFilterConfig {

    @Bean
    CommonsRequestLoggingFilter logFilter(FilterChainProxy filterChainProxy) {

        log.info("Filter chain: {}", filterChainProxy.getFilterChains());

        var filter = new PostOnlyRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeHeaders(true);
        filter.setIncludePayload(true);
        filter.setIncludeQueryString(true);
        filter.setMaxPayloadLength(10000);
        filter.setAfterMessagePrefix("REQUEST: ");
        return filter;
    }

    private static class PostOnlyRequestLoggingFilter extends CommonsRequestLoggingFilter {

        @Override
        protected boolean shouldLog(HttpServletRequest request) {
            return "POST".equalsIgnoreCase(request.getMethod());
        }

    }

}