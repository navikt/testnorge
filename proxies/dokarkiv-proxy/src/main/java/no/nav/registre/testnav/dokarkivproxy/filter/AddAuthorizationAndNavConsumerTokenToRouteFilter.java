package no.nav.registre.testnav.dokarkivproxy.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

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

        var token = generateToken.getToken();
        log.info("Injecter bearer token i requesten ({}...)...", token.substring(0, 3));
        ctx.addZuulRequestHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        ctx.addZuulRequestHeader(HEADER_NAV_CONSUMER_TOKEN, "Bearer " + token);
        log.info("Token injectert i requesten.");
        return null;
    }
}
