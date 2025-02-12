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
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticBestillingStrategyMapping implements MappingStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public void register(MapperFactory factory) {

        // Denne brukes ved initiell indexering av eksisterende bestillinger
        factory.classMap(Bestilling.class, ElasticBestilling.class)
                .customize(new CustomMapper<>() {
                               @Override
                               public void mapAtoB(Bestilling bestilling, ElasticBestilling elasticBestilling, MappingContext context) {

                                   try {
                                       elasticBestilling.setIgnore(isBlank(bestilling.getBestKriterier()) ||
                                               "{}".equals(bestilling.getBestKriterier()) ||
                                               bestilling.getProgresser().stream().noneMatch(BestillingProgress::isIdentGyldig));

                                       if (!elasticBestilling.isIgnore()) {

                                           var dollyBestilling = objectMapper.readValue(bestilling.getBestKriterier(), RsDollyBestilling.class);
                                           mapperFacade.map(dollyBestilling, elasticBestilling);

                                           elasticBestilling.setIdenter(bestilling.getProgresser().stream()
                                                   .filter(BestillingProgress::isIdentGyldig)
                                                   .map(BestillingProgress::getIdent)
                                                   .toList());
                                       }

                                   } catch (JsonProcessingException |
                                            IllegalArgumentException e) {

                                       elasticBestilling.setIgnore(true);
                                       log.warn("Kunne ikke konvertere fra JSON for bestilling-ID={}", bestilling.getId());

                                   } finally {

                                       elasticBestilling.setId(bestilling.getId());
                                   }
                               }
                           }
                )
                .register();

        // Denne brukes ved fortløpende nyoppretting av bestillinger
        factory.classMap(RsDollyBestilling.class, ElasticBestilling.class)
                .customize(new CustomMapper<>() {
                               @Override
                               public void mapAtoB(RsDollyBestilling bestilling, ElasticBestilling elasticBestilling, MappingContext context) {

                                   elasticBestilling.getDokarkiv()
                                           .forEach(arkiv -> arkiv.getDokumenter()
                                                   .forEach(dokument -> dokument.getDokumentvarianter()
                                                           .forEach(dokumentVariant -> dokumentVariant.setFysiskDokument(null))));

                                   if (nonNull(elasticBestilling.getHistark())) {
                                       elasticBestilling.getHistark().getDokumenter()
                                               .forEach(dokument -> dokument.setFysiskDokument(null));
                                   }
                               }
                           }
                )
                .byDefault()
                .register();
    }
}