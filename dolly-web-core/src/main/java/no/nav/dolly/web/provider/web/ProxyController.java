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
@RequestMapping("/api")
public class ProxyController {

    public static final String API_URI = "/api/v1";
    public static final String PROXY_URI = "/api/proxy";

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

    @Value("${fagsystem.inntektstub.url}")
    private String inntektstubUrl;

    private final ProxyService proxyService;

    @RequestMapping("/v1/**")
    public ResponseEntity<String> dollyProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, dollyUrl + API_URI, API_URI );

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/proxy/arena/**")
    public ResponseEntity<String> arenaProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, arenaUrl + API_URI, PROXY_URI + "/arena");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/proxy/inst/**")
    public ResponseEntity<String> instProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, instUrl + API_URI, PROXY_URI + "/inst");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/proxy/krr/**")
    public ResponseEntity<String> krrProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, krrUrl + API_URI, PROXY_URI + "/krr");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/proxy/sigrun/**")
    public ResponseEntity<String> sigrunProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, sigrunUrl + API_URI, PROXY_URI + "/sigrun");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/proxy/tpsf/**")
    public ResponseEntity<String> tpsfProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, tpsfUrl + API_URI, PROXY_URI + "/tpsf");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/proxy/kontaktinfo/**")
    public ResponseEntity<String> kontaktInfoProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, tpsfUrl + "/api", PROXY_URI + "/kontaktinfo");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/proxy/udi/**")
    public ResponseEntity<String> udiProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, udiUrl + API_URI, PROXY_URI + "/udi");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/proxy/aareg/**")
    public ResponseEntity<String> aaaregProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, aaregUrl + API_URI, PROXY_URI + "/aareg");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/proxy/popp/**")
    public ResponseEntity<String> poppProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, poppUrl + API_URI, PROXY_URI + "/popp");

        return proxyService.proxyRequest(body, method, request, requestURL);
    }

    @RequestMapping("/proxy/inntektstub/**")
    public ResponseEntity<String> inntektstubProxy(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String requestURL = createURL(request, inntektstubUrl + "/api/v2", PROXY_URI + "/inntektstub");
        System.out.println(requestURL);
        return proxyService.proxyRequest(body, method, request, requestURL);
    }

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
