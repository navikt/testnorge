package no.nav.testnav.proxies.pensjontestdatafacadeproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class PensjonTestdataFacadeProxyApplicationStarter {
    final Logger logger = LoggerFactory.getLogger(PensjonTestdataFacadeProxyApplicationStarter.class);

    public static void main(String[] args) {
        SpringApplication.run(PensjonTestdataFacadeProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(spec -> spec
                        .path("/api/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                //.removeRequestHeader(HttpHeaders.AUTHORIZATION)
                                .addRequestHeader(HttpHeaders.AUTHORIZATION, "dolly")
                                //.addRequestHeader("Nav-Call-Id", "dolly")
                                //.addRequestHeader("Nav-Consumer-Id", "dolly")
                        ) //Auth header er required men sjekkes ikke utover det
                        .uri("http://pensjon-testdata-facade.pensjontestdata.svc.nais.local/"))
                .build();
    }

//    @Component
//    public class LoggingGlobalPreFilter implements GlobalFilter {
//
//        final Logger logger = LoggerFactory.getLogger(LoggingGlobalPreFilter.class);
//
//        @Override
//        public Mono<Void> filter(
//                ServerWebExchange exchange,
//                GatewayFilterChain chain) {
//            logger.info("Global Pre Filter executed");
//            logger.info("####### headers: " + exchange.getRequest().getHeaders());
//            logger.info("####### headers: " + exchange.getRequest().getRemoteAddress() + " " + exchange.getRequest().getPath());
//            return chain.filter(exchange);
//        }
//    }
//
//    @Bean
//    public GlobalFilter postGlobalFilter() {
//        return (exchange, chain) -> {
//            return chain.filter(exchange)
//                    .then(Mono.fromRunnable(() -> {
//                        logger.info("####### response headers: " + exchange.getResponse().getHeaders());
//                    }));
//        };
//    }
}
