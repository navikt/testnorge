package no.nav.dolly.mapper.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.entity.bestilling.MalBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestilling;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
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

                        log.info("Mapper malbestilling med Id: {}", kilde.getId());

                        destinasjon.setId(kilde.getId());
                        destinasjon.setMalNavn(kilde.getMalNavn());
                        destinasjon.setMiljoer(kilde.getMiljoer());
                        destinasjon.setSistOppdatert(kilde.getSistOppdatert());
                        try {
                            destinasjon.setMalBestilling(objectMapper.readTree(kilde.getMalBestilling()));

                        } catch (JsonProcessingException e) {
                            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                        }
                    }
                })
                .register();
    }
}
