package no.nav.testnav.safproxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import java.util.function.Supplier;

import no.nav.registre.testnorge.libs.service.StsOidcTokenService;
import no.nav.testnav.safproxy.filter.AddRequestHeaderGatewayFilterFactory;
import no.nav.testnav.safproxy.config.GetHeader;

@SpringBootApplication
public class SafProxyApplicationStarter {

    @Bean
    public StsOidcTokenService stsPreprodOidcTokenService(
            @Value("${sts.preprod.token.provider.url}") String url,
            @Value("${sts.preprod.token.provider.username}") String username,
            @Value("${sts.preprod.token.provider.password}") String password
    ) {
        return new StsOidcTokenService(url, username, password);
    }

//    @Bean
//    public StsOidcTokenService stsTestOidcTokenService(
//            @Value("${sts.test.token.provider.url}") String url,
//            @Value("${sts.test.token.provider.username}") String username,
//            @Value("${sts.test.token.provider.password}") String password
//    ) {
//        return new StsOidcTokenService(url, username, password);
//    }


    public static void main(String[] args) {
        SpringApplication.run(SafProxyApplicationStarter.class, args);
    }


    private GatewayFilter addAuthenticationHeaderFilter(Supplier<String> tokenService) {
        var getHeader = new GetHeader(() -> HttpHeaders.AUTHORIZATION, () -> "Bearer " + tokenService.get());
        return new AddRequestHeaderGatewayFilterFactory().apply(getHeader);
    }


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, StsOidcTokenService stsPreprodOidcTokenService) {
        var preprodFilter = addAuthenticationHeaderFilter(stsPreprodOidcTokenService::getToken);
        //var testFilter = addAuthenticationHeaderFilter(stsTestOidcTokenService::getToken);
        return builder
                .routes()
                .route("saf-q2", spec -> spec
                        .path("/q2/**")
                        .filters(filterSpec -> filterSpec
                                .rewritePath("/q2/(?<segment>.*)", "/${segment}")
                                .filter(preprodFilter)
                        ).uri("https://saf-q2.dev.adeo.no/")
                ).build();
    }
}