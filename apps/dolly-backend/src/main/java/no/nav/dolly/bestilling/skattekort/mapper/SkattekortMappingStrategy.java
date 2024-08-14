package no.nav.dolly.bestilling.skattekort.mapper;

import ma.glasnost.orika.MapperFactory;
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
                .register();
    }
}
