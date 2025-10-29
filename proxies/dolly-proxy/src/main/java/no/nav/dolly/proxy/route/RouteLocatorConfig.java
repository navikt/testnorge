package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class RouteLocatorConfig {

    private final Fullmakt fullmakt;
    private final Histark histark;
    private final Inntektstub inntektstub;
    private final Sigrunstub sigrunstub;
    private final Skjermingsregister skjermingsregister;
    private final Udistub udistub;

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route("fullmakt", fullmakt.build())
                .route("histark", histark.build())
                .route("inntektstub", inntektstub.build())
                .route("sigrunstub", sigrunstub.build())
                .route("skjermingsregister", skjermingsregister.build())
                .route("udistub", udistub.build())
                .build();
    }

}