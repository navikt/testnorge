package no.nav.dolly.web.provider.web;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

    @Value("${fagsystem.pensjonforvalter.url}")
    private String poppUrl;

    @Value("${fagsystem.aareg.url}")
    private String aaregUrl;

    private final ProxyService proxyService;

    @RequestMapping("/dolly/**")
    public ResponseEntity<String> dollyProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, dollyUrl + API_URI, API_URI + "/dolly");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/arena/**")
    public ResponseEntity<String> arenaProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, arenaUrl + API_URI, API_URI + "/arena");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/inst/**")
    public ResponseEntity<String> instProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, instUrl + API_URI, API_URI + "/inst");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/krr/**")
    public ResponseEntity<String> krrProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, krrUrl + API_URI, API_URI + "/krr");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/sigrun/**")
    public ResponseEntity<String> sigrunProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, sigrunUrl + API_URI, API_URI + "/sigrun");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/tpsf/**")
    public ResponseEntity<String> tpsfProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, tpsfUrl + API_URI, API_URI + "/tpsf");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/kontaktinfo/**")
    public ResponseEntity<String> kontaktInfoProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, tpsfUrl + "/api", API_URI + "/kontaktinfo");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/udi/**")
    public ResponseEntity<String> udiProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, udiUrl + API_URI, API_URI + "/udi");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/aareg/**")
    public ResponseEntity<String> aaaregProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, aaregUrl + API_URI, API_URI + "/aaregproxy");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/pensjon/**")
    public ResponseEntity<String> poppProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, poppUrl + API_URI, API_URI + "/pensjon");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }
//
//    @RequestMapping("/inntektstub/**")
//    public ResponseEntity<String> poppProxy(
//            @RequestBody(required = false) String body,
//            HttpMethod method,
//            HttpServletRequest request) throws UnsupportedEncodingException {
//
//        String requestURL = createURL(request, inntektstubUrl + API_URI, API_URI + "/inntektstub");
//
//        return proxyService.proxyRequest(body, method, request, requestURL);
//    }
//
//    @RequestMapping("/pdlf/**")
//    public ResponseEntity<String> poppProxy(
//            @RequestBody(required = false) String body,
//            HttpMethod method,
//            HttpServletRequest request) throws UnsupportedEncodingException {
//
//        String requestURL = createURL(request, pdlfUrl + API_URI, API_URI + "/pdlf");
//
//        return proxyService.proxyRequest(body, method, request, requestURL);
//    }

    private static String createURL(HttpServletRequest request, String host, String splitUri)
            throws UnsupportedEncodingException {

        if (request.getRequestURI().split(splitUri).length < 2) {
            throw new UnsupportedEncodingException(format("Incomplete url: %s", request.getRequestURI()));
        }
        String queryString = "";
        if (StringUtils.isNotBlank(request.getQueryString())) {
            queryString = URLDecoder.decode("?" + request.getQueryString(), StandardCharsets.UTF_8.name());
        }

        return format("%s%s%s", host, request.getRequestURI().split(splitUri)[1], queryString);
    }
}
