package no.nav.dolly.web.provider.web;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/**")
public class ProxyController {

    @Value("${dolly.url}")
    private String dollyUrl;

    private final ProxyService proxyService;

    @RequestMapping
    public ResponseEntity<String> dollyProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, dollyUrl);

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    private static String createURL(HttpServletRequest request, String host)
            throws UnsupportedEncodingException {

        String url = "/api/v1";

        if (request.getRequestURI().split(url).length < 2) {
            throw new UnsupportedEncodingException(format("Incomplete url: %s", url));
        }
        String queryString = "";
        if (request.getQueryString() != null) {
            queryString = URLDecoder.decode("?" + request.getQueryString(), StandardCharsets.UTF_8.name());
        }
        return format("%s%s%s", host, request.getRequestURI(), queryString);
    }
}
