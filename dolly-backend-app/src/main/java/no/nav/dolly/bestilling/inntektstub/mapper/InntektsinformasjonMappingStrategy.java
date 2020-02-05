package no.nav.dolly.bestilling.inntektstub.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.inntektstub.domain.Inntekt;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.InntektsinformasjonWrapper;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class InntektsinformasjonMappingStrategy implements MappingStrategy {

    private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols();
    static {
        DECIMAL_FORMAT_SYMBOLS.setDecimalSeparator('.');
    }
    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.##", DECIMAL_FORMAT_SYMBOLS);
    private static final DateTimeFormatter YEAR_MONTH_PTR = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(InntektMultiplierWrapper.class, InntektsinformasjonWrapper.class)
                .customize(new CustomMapper<InntektMultiplierWrapper, InntektsinformasjonWrapper>() {
                    @Override
                    public void mapAtoB(InntektMultiplierWrapper inntektMultiplierWrapper, InntektsinformasjonWrapper inntektsinformasjonWrapper, MappingContext context) {

                        if (nonNull(inntektMultiplierWrapper.getAntallMaaneder())) {

                            LocalDateTime yearMonth = inntektMultiplierWrapper.getInntektsinformasjon().get(0).getAarMaaned();
                            Double percentMultiplier = getPercentMultiplier(inntektMultiplierWrapper);
                            Double increaser = initIncreaser(inntektMultiplierWrapper, percentMultiplier);

                            for (int i = 0; i < inntektMultiplierWrapper.getAntallMaaneder(); i++) {

                                Inntektsinformasjon inntektsinformasjon = mapperFacade.map(
                                        inntektMultiplierWrapper.getInntektsinformasjon().get(0), Inntektsinformasjon.class);
                                inntektsinformasjon.setAarMaaned(yearMonth.format(YEAR_MONTH_PTR));

                                increaser = getIncreaser(increaser, percentMultiplier, i);

                                prepareInntekt(yearMonth, increaser, inntektsinformasjon);

                                yearMonth = yearMonth.plusMonths(1);
                                inntektsinformasjonWrapper.getInntektsinformasjon().add(inntektsinformasjon);
                            }
                        } else {

                            inntektsinformasjonWrapper.getInntektsinformasjon().addAll(
                                    mapperFacade.mapAsList(inntektMultiplierWrapper.getInntektsinformasjon(), Inntektsinformasjon.class));
                        }
                    }

                    private Double initIncreaser(InntektMultiplierWrapper inntektMultiplierWrapper, Double percentMultiplier) {
                        return nonNull(inntektMultiplierWrapper.getProsentOekningPerAaar()) ? 1 / percentMultiplier : null;
                    }

                    private Double getPercentMultiplier(InntektMultiplierWrapper inntektMultiplierWrapper) {
                        return nonNull(inntektMultiplierWrapper.getProsentOekningPerAaar()) ?
                                1 + inntektMultiplierWrapper.getProsentOekningPerAaar() / 100 : null;
                    }

                    private double getIncreaser(Double increaser, Double percentMultiplier, int i) {
                        return nonNull(increaser) && i % 12 == 0 ? increaser * percentMultiplier : increaser;
                    }

                    private void prepareInntekt(LocalDateTime yearMonth, Double increaser, Inntektsinformasjon inntektsinformasjon) {
                        for (Inntekt inntekt : inntektsinformasjon.getInntektsliste()) {
                            Double beloep = isNull(increaser) ? inntekt.getBeloep() : inntekt.getBeloep() * increaser;
                            inntekt.setBeloep(Double.valueOf(DECIMAL_FORMATTER.format(beloep)));
                            inntekt.setStartOpptjeningsperiode(yearMonth.toLocalDate());
                            inntekt.setSluttOpptjeningsperiode(yearMonth.plusMonths(1).minusHours(1).toLocalDate());
                        }
                    }
                })
                .register();

        factory.classMap(RsInntektsinformasjon.class, Inntektsinformasjon.class)
                .customize(new CustomMapper<RsInntektsinformasjon, Inntektsinformasjon>() {
                    @Override
                    public void mapAtoB(RsInntektsinformasjon rsInntektsinformasjon, Inntektsinformasjon inntektsinformasjon, MappingContext context) {

                        inntektsinformasjon.setAarMaaned(rsInntektsinformasjon.getAarMaaned().format(YEAR_MONTH_PTR));
                    }
                })
                .exclude("aarMaaned")
                .byDefault()
                .register();
    }
}
