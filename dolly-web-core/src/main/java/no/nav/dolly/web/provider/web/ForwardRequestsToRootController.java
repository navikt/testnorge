package no.nav.dolly.web.provider.web;

import static java.lang.String.format;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Controller
public class ForwardRequestsToRootController {

    @RequestMapping(value = {"/profil/**", "/team/**", "/gruppe/**", "/maler/**", "/tpsendring/**"})
    public String forwardToIndex(@RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {
        return createForwardURL( request);
    }

    public static String createForwardURL(HttpServletRequest request)
            throws UnsupportedEncodingException {

        String url = "/index";

        if (request.getRequestURI().split(url).length < 2) {
            throw new UnsupportedEncodingException(format("Incomplete url: %s", url));
        }
        String queryString = "";
        if (request.getQueryString() != null) {
            queryString = URLDecoder.decode("?" + request.getQueryString(), StandardCharsets.UTF_8.name());
        }
        return format("%s%s", request.getRequestURI().split(url)[1], queryString);
    }
}
