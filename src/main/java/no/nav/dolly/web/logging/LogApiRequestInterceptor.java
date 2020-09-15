package no.nav.dolly.web.logging;

import javax.servlet.http.HttpServletRequest;

public class LogApiRequestInterceptor extends LogRequestInterceptor {
    @Override
    boolean shouldLogRequest(HttpServletRequest request) {
        if(request.getMethod().equals("GET") && request.getRequestURI().matches("^\\/api\\/v1\\/bestilling\\/\\d+$")){
            //Ignorer bestilling ping
            return false;
        }

        return request.getRequestURI().matches("^\\/api\\/.*$");
    }
}
