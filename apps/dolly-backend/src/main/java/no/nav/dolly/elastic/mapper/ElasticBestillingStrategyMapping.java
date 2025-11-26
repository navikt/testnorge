package no.nav.dolly.elastic.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.elastic.BestillingDokument;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticBestillingStrategyMapping implements MappingStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public void register(MapperFactory factory) {

        // Denne brukes ved initiell indexering av eksisterende bestillinger
        factory.classMap(Bestilling.class, BestillingDokument.class)
                .customize(new CustomMapper<>() {
                               @Override
                               public void mapAtoB(Bestilling bestilling, BestillingDokument bestillingDokument, MappingContext context) {

                                   try {
                                       bestillingDokument.setIgnore(isBlank(bestilling.getBestKriterier()) ||
                                               "{}".equals(bestilling.getBestKriterier()) ||
                                               bestilling.getProgresser().stream().noneMatch(BestillingProgress::isIdentGyldig));

                                       if (!bestillingDokument.isIgnore()) {

                                           var dollyBestilling = objectMapper.readValue(bestilling.getBestKriterier(), RsDollyBestilling.class);
                                           mapperFacade.map(dollyBestilling, bestillingDokument);

                                           bestillingDokument.setMiljoer(isNotBlank(bestilling.getMiljoer()) ?
                                                   List.of(bestilling.getMiljoer().split(",")) : null);

                                           bestillingDokument.setIdenter(bestilling.getProgresser().stream()
                                                   .filter(BestillingProgress::isIdentGyldig)
                                                   .map(BestillingProgress::getIdent)
                                                   .toList());
                                       }

                                   } catch (JsonProcessingException |
                                            IllegalArgumentException e) {

                                       bestillingDokument.setIgnore(true);
                                       log.warn("Kunne ikke konvertere fra JSON for bestilling-ID={}", bestilling.getId());

                                   } finally {

                                       bestillingDokument.setId(bestilling.getId());
                                   }
                               }
                           }
                )
                .register();

        // Denne brukes ved fortløpende nyoppretting av bestillinger
        factory.classMap(RsDollyBestilling.class, BestillingDokument.class)
                .customize(new CustomMapper<>() {
                               @Override
                               public void mapAtoB(RsDollyBestilling bestilling, BestillingDokument bestillingDokument, MappingContext context) {

                                   bestillingDokument.getDokarkiv()
                                           .forEach(arkiv -> arkiv.getDokumenter()
                                                   .forEach(dokument -> dokument.getDokumentvarianter()
                                                           .forEach(dokumentVariant -> dokumentVariant.setFysiskDokument(null))));

                                   if (nonNull(bestillingDokument.getHistark())) {
                                       bestillingDokument.getHistark().getDokumenter()
                                               .forEach(dokument -> dokument.setFysiskDokument(null));
                                   }
                               }
                           }
                )
                .byDefault()
                .register();
    }
}