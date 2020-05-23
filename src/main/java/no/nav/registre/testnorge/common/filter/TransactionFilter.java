package no.nav.registre.testnorge.common.filter;

import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import no.nav.registre.testnorge.common.log.RequestLogger;
import no.nav.registre.testnorge.common.log.ResponseLogger;

public class TransactionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        RequestLogger requestLog = new RequestLogger(requestWrapper);
        ResponseLogger responseLog = new ResponseLogger(responseWrapper);

        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            requestLog.log();
            responseLog.log();
        }
    }

    @Override
    public void destroy() {
    }
}
