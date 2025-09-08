package no.nav.dolly.mapper.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.projection.MalBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestilling;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class MalBestillingMappingStrategy implements MappingStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(MalBestilling.class, RsMalBestilling.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(MalBestilling kilde, RsMalBestilling destinasjon, MappingContext context) {

                        destinasjon.setId(kilde.getId());
                        destinasjon.setMalNavn(kilde.getMalnavn());
                        destinasjon.setMiljoer(kilde.getMiljoer());
                        destinasjon.setSistOppdatert(kilde.getSistoppdatert());
                        try {
                            destinasjon.setMalBestilling(objectMapper.readTree(kilde.getMalbestilling()));

                        } catch (JsonProcessingException e) {
                            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                        }
                    }
                })
                .register();
    }
}