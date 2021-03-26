package no.nav.registre.testnorge.aaregproxy.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Set;

@Slf4j
public class AddAuthorizationAndNavConsumerTokenToRouteFilter extends ZuulFilter {
    public static final String HEADER_NAV_CONSUMER_TOKEN = "Nav-Consumer-Token";

    private final GenerateToken generateToken;
    private final Set<String> routes;

    public AddAuthorizationAndNavConsumerTokenToRouteFilter(GenerateToken generateToken, String... routes) {
        this.generateToken = generateToken;
        this.routes = Set.of(routes);
    }

    public RestTemplate restTemplate;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 6;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return (ctx.get("proxy") != null) && routes.stream().anyMatch(value -> ctx.get("proxy").equals(value));
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        log.info("Injecter bearer token i requesten.");
        var token = generateToken.getToken();

        log.info("Call endpoint");
        var exchange = restTemplate.exchange(RequestEntity.get(URI.create("https://modapp-q0.adeo.no/aareg-services/api/v1/arbeidsgiver/arbeidsforhold")).build(), String.class);
        log.info("body: {}", exchange.getBody());

        ctx.addZuulRequestHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        ctx.addZuulRequestHeader(HEADER_NAV_CONSUMER_TOKEN, "Bearer " + token);
        return null;
    }
}
