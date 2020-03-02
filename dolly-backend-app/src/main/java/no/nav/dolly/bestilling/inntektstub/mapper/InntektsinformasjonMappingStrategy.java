package no.nav.dolly.bestilling.inntektstub.mapper;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.InntektsinformasjonWrapper;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class InntektsinformasjonMappingStrategy implements MappingStrategy {

    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(InntektMultiplierWrapper.class, InntektsinformasjonWrapper.class)
                .customize(new CustomMapper<InntektMultiplierWrapper, InntektsinformasjonWrapper>() {
                    @Override
                    public void mapAtoB(InntektMultiplierWrapper inntektMultiplierWrapper, InntektsinformasjonWrapper inntektsinformasjonWrapper, MappingContext context) {

                        inntektMultiplierWrapper.getInntektsinformasjon().forEach(inntektsinformasjon -> {

                            LocalDateTime yearMonth = inntektMultiplierWrapper.getInntektsinformasjon().get(0).getStartAarMaaned();
                            int antallMaaneder = isNull(inntektsinformasjon.getAntallMaaneder()) || inntektsinformasjon.getAntallMaaneder() < 0 ? 1 :
                                    inntektsinformasjon.getAntallMaaneder();

                            do {
                                Inntektsinformasjon inntektsinformasjon1 = mapperFacade.map(
                                        inntektMultiplierWrapper.getInntektsinformasjon().get(0), Inntektsinformasjon.class);

                                inntektsinformasjon1.setAarMaaned(yearMonth.format(YEAR_MONTH_FORMAT));
                                yearMonth = yearMonth.plusMonths(1);

                                inntektsinformasjonWrapper.getInntektsinformasjon().add(inntektsinformasjon1);

                            } while (--antallMaaneder > 0);
                        });
                    }
                })
                .register();
    }
}
