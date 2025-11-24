package no.nav.dolly.mapper.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingUtenFavoritter;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MalBestillingUtenFavoritterMappingStrategy implements MappingStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(BestillingMal.class, RsMalBestillingUtenFavoritter.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BestillingMal kilde, RsMalBestillingUtenFavoritter destinasjon, MappingContext context) {

                        try {
                            destinasjon.setId(kilde.getId());
                            destinasjon.setMiljoer(kilde.getMiljoer());
                            destinasjon.setMalNavn(kilde.getMalNavn());
                            destinasjon.setBestilling(objectMapper.readTree(kilde.getBestKriterier()));
                            destinasjon.setBruker(mapperFacade.map(kilde.getBruker(), RsBrukerUtenFavoritter.class));
                            destinasjon.setSistOppdatert(kilde.getSistOppdatert());

                        } catch (JsonProcessingException e) {
                            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                        }
                    }
                })
                .register();
    }
}
