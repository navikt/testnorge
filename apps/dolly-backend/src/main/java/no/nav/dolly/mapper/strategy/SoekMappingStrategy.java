package no.nav.dolly.mapper.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Soek;
import no.nav.dolly.domain.resultset.RsSoek;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoekMappingStrategy implements MappingStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Soek.class, RsSoek.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Soek soek, RsSoek rsSoek, MappingContext context) {

                        rsSoek.setOpprettet(soek.getOpprettetTidspunkt());
                        try {
                            rsSoek.setSoekVerdi(objectMapper.readTree(soek.getSoekVerdi()));
                        } catch (JsonProcessingException e) {
                            rsSoek.setFeilmelding(e.getMessage());
                        }
                     }
                })
                .exclude("soekVerdi")
                .byDefault()
                .register();
    }
}