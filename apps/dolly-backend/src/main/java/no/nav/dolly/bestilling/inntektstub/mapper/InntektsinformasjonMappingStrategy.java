package no.nav.dolly.bestilling.inntektstub.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.inntektstub.domain.Inntekt;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekter;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

        factory.classMap(no.nav.dolly.domain.resultset.inntektstub.Inntekt.class, Inntekt.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(no.nav.dolly.domain.resultset.inntektstub.Inntekt kilde, Inntekt destinasjon, MappingContext context) {
                        var periode = (YearMonth) context.getProperty(PERIODE);
                        if (nonNull(periode)) {
                            destinasjon.setStartOpptjeningsperiode(
                                    periode.atDay(isNull(kilde.getStartOpptjeningsperiode()) ? 1 :
                                            getGyldigDagIMaaned(periode, kilde.getStartOpptjeningsperiode())));
                            destinasjon.setSluttOpptjeningsperiode(periode.atDay(isNull(kilde.getSluttOpptjeningsperiode()) ? periode.atEndOfMonth().getDayOfMonth() :
                                    getGyldigDagIMaaned(periode, kilde.getSluttOpptjeningsperiode())));
                        }
                    }
                })
                .byDefault()
                .register();
    }

    private static int getGyldigDagIMaaned(YearMonth periode, LocalDateTime opptjeningDato) {

        return Math.min(opptjeningDato.getDayOfMonth(), periode.atEndOfMonth().getDayOfMonth());
    }
}
