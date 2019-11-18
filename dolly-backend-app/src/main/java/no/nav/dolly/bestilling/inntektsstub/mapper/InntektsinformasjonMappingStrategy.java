package no.nav.dolly.bestilling.inntektsstub.mapper;

import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.inntektsstub.domain.Inntektsinformasjon;
import no.nav.dolly.domain.resultset.inntektsstub.RsInntektsinformasjon;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class InntektsinformasjonMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsInntektsinformasjon.class, Inntektsinformasjon.class)
                .customize(new CustomMapper<RsInntektsinformasjon, Inntektsinformasjon>() {
                    @Override
                    public void mapAtoB(RsInntektsinformasjon rsInntektsinformasjon, Inntektsinformasjon inntektsinformasjon, MappingContext context) {

                        inntektsinformasjon.setAarMaaned(rsInntektsinformasjon.getAarMaaned().format(DateTimeFormatter.ofPattern("yyyy-MM")));
                        inntektsinformasjon.setVersjon(0);
                    }
                })
                .exclude("aarMaaned")
                .byDefault()
                .register();
    }
}
