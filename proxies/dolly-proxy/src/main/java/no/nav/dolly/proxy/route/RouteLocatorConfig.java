package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class RouteLocatorConfig {

    private final Brregstub br;
    private final Ereg ereg;
    private final Fullmakt fullmakt;
    private final Histark histark;
    private final Inntektstub inntektstub;
    private final Inst inst;
    private final Kontoregister kontoregister;
    private final Krrstub krrstub;
    private final Norg2 norg2;
    private final Pensjon pensjontestdataFacade;
    private final Sigrunstub sigrunstub;
    private final Skjermingsregister skjermingsregister;
    private final Udistub udistub;

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route("brregstub", br.build())
                .route("ereg-q1", ereg.build(Ereg.SpecialCase.Q1))
                .route("ereg-q2", ereg.build(Ereg.SpecialCase.Q2))
                .route("ereg-q4", ereg.build(Ereg.SpecialCase.Q4))
                .route("fullmakt", fullmakt.build())
                .route("histark", histark.build())
                .route("inntektstub", inntektstub.build())
                .route("inst", inst.build())
                .route("kontoregister", kontoregister.build())
                .route("krrstub", krrstub.build())
                .route("norg2", norg2.build())
                .route("pensjontestdatafacade", pensjontestdataFacade.build())
                .route("pensjontestdatafacade", pensjontestdataFacade.build(Pensjon.SpecialCase.AFP_Q1))
                .route("pensjontestdatafacade", pensjontestdataFacade.build(Pensjon.SpecialCase.AFP_Q2))
                .route("pensjontestdatafacade", pensjontestdataFacade.build(Pensjon.SpecialCase.SAMBOER_Q1))
                .route("pensjontestdatafacade", pensjontestdataFacade.build(Pensjon.SpecialCase.SAMBOER_Q2))
                .route("sigrunstub", sigrunstub.build())
                .route("skjermingsregister", skjermingsregister.build())
                .route("udistub", udistub.build())
                .build();
    }

}