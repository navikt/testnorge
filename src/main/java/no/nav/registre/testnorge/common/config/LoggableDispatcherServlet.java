package no.nav.registre.testnorge.common.config;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.nav.registre.testnorge.common.log.RequestLog;
import no.nav.registre.testnorge.common.log.ResponseLog;

public class LoggableDispatcherServlet extends DispatcherServlet {

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        RequestLog requestLog = new RequestLog(requestWrapper);
        ResponseLog responseLog = new ResponseLog(responseWrapper);
        try {

            super.doDispatch(requestWrapper, responseWrapper);
        } finally {
            requestLog.log();
            responseLog.log();
        }
    }

}
