package no.nav.dolly.bestilling.inntektstub.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.inntektstub.domain.Inntekt;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekt;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekter;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class InntektsinformasjonMappingStrategy implements MappingStrategy {

    private static final String PERIODE = "periode";
    private static final String IDENT = "ident";
    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsInntekter.class, Inntektsinformasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntekter kilde, Inntektsinformasjon resultat, MappingContext context) {

                        var yearMonth = (YearMonth) context.getProperty(PERIODE);
                        resultat.setAarMaaned(yearMonth.format(YEAR_MONTH_FORMAT));
                        resultat.setNorskIdent((String) context.getProperty(IDENT));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInntektsinformasjon.class, Inntektsinformasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsinformasjon kilde, Inntektsinformasjon resultat, MappingContext context) {

                        var yearMonth = (YearMonth) context.getProperty(PERIODE);
                        resultat.setAarMaaned(yearMonth.format(YEAR_MONTH_FORMAT));
                        resultat.setNorskIdent((String) context.getProperty(IDENT));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInntekt.class, Inntekt.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntekt kilde, Inntekt destinasjon, MappingContext context) {
                        var periode = (YearMonth) context.getProperty(PERIODE);
                        if (nonNull(periode)) {
                            if (isNull(kilde.getStartOpptjeningsperiode())) {
                                destinasjon.setStartOpptjeningsperiode(periode.atDay(1));
                            }
                            if (isNull(kilde.getSluttOpptjeningsperiode())) {
                                destinasjon.setSluttOpptjeningsperiode(periode.atEndOfMonth());
                            }
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
