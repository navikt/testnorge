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
@RequestMapping("/api/v1")
public class ProxyController {

    public static final String API_URI = "/api/v1";

    @Value("${dolly.url}")
    private String dollyUrl;
    @Value("${fagsystem.arena.url}")
    private String arenaUrl;
    @Value("${fagsystem.instdata.url}")
    private String instUrl;
    @Value("${fagsystem.krrstub.url}")
    private String krrUrl;
    @Value("${fagsystem.sigrunstub.url}")
    private String sigrunUrl;
    @Value("${fagsystem.tpsf.url}")
    private String tpsfUrl;
    @Value("${fagsystem.udistub.url}")
    private String udiUrl;

    private final ProxyService proxyService;

    @RequestMapping("/dolly/**")
    public ResponseEntity<String> dollyProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, dollyUrl, API_URI+"/dolly");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/arena/**")
    public ResponseEntity<String> arenaProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, arenaUrl, API_URI+"/arena");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/inst/**")
    public ResponseEntity<String> instProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, instUrl, API_URI+"/inst");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/krr/**")
    public ResponseEntity<String> krrProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, krrUrl, API_URI+"/krr");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/sigrun/**")
    public ResponseEntity<String> sigrunProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, sigrunUrl, API_URI+"/sigrun");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

//    @RequestMapping("/tpsf/**")
//    public ResponseEntity<String> tpsfProxy(
//            @RequestBody(required = false) String body,
//            HttpMethod method,
//            HttpServletRequest request) throws UnsupportedEncodingException {
//
//        String requestURL = createURL(request, tpsfUrl, API_URI+"/tpsf");
//
//        return proxyService.proxyRequest(body, method, request, requestURL);
//    }
//
//    @RequestMapping("/kontaktinfo/**")
//    public ResponseEntity<String> kontaktInfoProxy(
//            @RequestBody(required = false) String body,
//            HttpMethod method,
//            HttpServletRequest request) throws UnsupportedEncodingException {
//
//        String requestURL = createURL(request, tpsfUrl.split("/v1")[0], API_URI+"/kontaktinfo");
//
//        return proxyService.proxyRequest(body, method, request, requestURL);
//    }

    @RequestMapping("/udi/**")
    public ResponseEntity<String> udiProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, udiUrl, API_URI+"/udi");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    private static String createURL(HttpServletRequest request, String host, String split_uri)
            throws UnsupportedEncodingException {


        if (request.getRequestURI().split(split_uri).length < 2) {
            throw new UnsupportedEncodingException(format("Incomplete url: %s", request.getRequestURI()));
        }
        String queryString = "";
        if (request.getQueryString() != null) {
            queryString = URLDecoder.decode("?" + request.getQueryString(), StandardCharsets.UTF_8.name());
        }

        return format("%s%s%s", host, request.getRequestURI().split(split_uri)[1], queryString);
    }
}
