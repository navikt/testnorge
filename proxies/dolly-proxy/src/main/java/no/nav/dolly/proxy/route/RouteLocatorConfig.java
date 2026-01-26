package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class RouteLocatorConfig {

    private final Aareg aareg;
    private final Arena arena;
    private final Batch batch;
    private final Brregstub brregstub;
    private final Dokarkiv dokarkiv;
    private final Ereg ereg;
    private final Fullmakt fullmakt;
    private final Histark histark;
    private final Inntektstub inntektstub;
    private final Inst inst;
    private final Kontoregister kontoregister;
    private final Krrstub krrstub;
    private final Medl medl;
    private final Norg2 norg2;
    private final Pdl pdl;
    private final Pensjon pensjon;
    private final Saf saf;
    private final Sigrunstub sigrunstub;
    private final Skjermingsregister skjermingsregister;
    private final Udistub udistub;

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route("aareg-read-q1", aareg.build(Aareg.SpecialCase.Q1, false))
                .route("aareg-read-q2", aareg.build(Aareg.SpecialCase.Q2, false))
                .route("aareg-read-q4", aareg.build(Aareg.SpecialCase.Q4, false))
                .route("aareg-write-q1", aareg.build(Aareg.SpecialCase.Q1, true))
                .route("aareg-write-q2", aareg.build(Aareg.SpecialCase.Q2, true))
                .route("aareg-write-q4", aareg.build(Aareg.SpecialCase.Q4, true))
                .route("arena", arena.build())
                .route("arena-q1", arena.build(Arena.SpecialCase.Q1))
                .route("arena-q2", arena.build(Arena.SpecialCase.Q2))
                .route("arena-q4", arena.build(Arena.SpecialCase.Q4))
                .route("batch", batch.build())
                .route("brregstub", brregstub.build())
                .route("dokarkiv-q1", dokarkiv.build(Dokarkiv.SpecialCase.Q1))
                .route("dokarkiv-q2", dokarkiv.build(Dokarkiv.SpecialCase.Q2))
                .route("dokarkiv-q4", dokarkiv.build(Dokarkiv.SpecialCase.Q4))
                .route("ereg-q1", ereg.build(Ereg.SpecialCase.Q1))
                .route("ereg-q2", ereg.build(Ereg.SpecialCase.Q2))
                .route("ereg-q4", ereg.build(Ereg.SpecialCase.Q4))
                .route("fullmakt", fullmakt.build())
                .route("histark", histark.build())
                .route("inntektstub", inntektstub.build())
                .route("inst", inst.build())
                .route("kontoregister", kontoregister.build())
                .route("krrstub", krrstub.build())
                .route("medl", medl.build())
                .route("norg2", norg2.build())
                .route("pdl-api", pdl.build(Pdl.SpecialCase.API))
                .route("pdl-api-q1", pdl.build(Pdl.SpecialCase.API_Q1))
                .route("pdl-identhendelse", pdl.build(Pdl.SpecialCase.IDENTHENDELSE))
                .route("pdl-testdata", pdl.build(Pdl.SpecialCase.TESTDATA))
                .route("pensjon", pensjon.build())
                .route("pensjon-afp-q1", pensjon.build(Pensjon.SpecialCase.AFP_Q1))
                .route("pensjon-afp-q2", pensjon.build(Pensjon.SpecialCase.AFP_Q2))
                .route("pensjon-samboer-q1", pensjon.build(Pensjon.SpecialCase.SAMBOER_Q1))
                .route("pensjon-samboer-q2", pensjon.build(Pensjon.SpecialCase.SAMBOER_Q2))
                .route("saf-q1", saf.build(Saf.SpecialCase.Q1))
                .route("saf-q2", saf.build(Saf.SpecialCase.Q2))
                .route("saf-q4", saf.build(Saf.SpecialCase.Q4))
                .route("sigrunstub", sigrunstub.build())
                .route("skjermingsregister", skjermingsregister.build())
                .route("udistub", udistub.build())
                .build();
    }

}