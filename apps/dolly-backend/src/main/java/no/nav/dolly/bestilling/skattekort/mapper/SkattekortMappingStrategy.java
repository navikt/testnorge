package no.nav.dolly.bestilling.skattekort.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class SkattekortMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(SkattekortRequestDTO.class, no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO.class)
                .mapNulls(false)
                .field("arbeidsgiverSkatt", "arbeidsgiver")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkattekortRequestDTO kilde, no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO destinasjon, MappingContext context) {
                        var ident = (String) context.getProperty("ident");
                        destinasjon.getArbeidsgiver()
                                .forEach(arbeidsgiverSkatt -> arbeidsgiverSkatt.getArbeidstaker()
                                        .forEach(arbeidstaker -> arbeidstaker.setArbeidstakeridentifikator(ident)));
                    }
                })
                .byDefault()
                .register();
    }
}
