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

        factory.classMap(RsDollyBestilling.class, ElasticBestilling.class)
                .customize(new CustomMapper<>() {
                               @Override
                               public void mapAtoB(RsDollyBestilling dollyBestilling, ElasticBestilling elasticBestilling, MappingContext context) {

                                   elasticBestilling.setPenInntekt(nonNull(dollyBestilling.getPensjonforvalter()) ?
                                           dollyBestilling.getPensjonforvalter().getInntekt() : null);

                                   elasticBestilling.setPenAlderspensjon(nonNull(dollyBestilling.getPensjonforvalter()) ?
                                           dollyBestilling.getPensjonforvalter().getAlderspensjon() : null);

                                   elasticBestilling.setPenUforetrygd(nonNull(dollyBestilling.getPensjonforvalter()) ?
                                           dollyBestilling.getPensjonforvalter().getUforetrygd() : null);

                                   elasticBestilling.setPenTp(nonNull(dollyBestilling.getPensjonforvalter()) ?
                                           dollyBestilling.getPensjonforvalter().getTp() : null);

                                   elasticBestilling.setSigrunInntekt(dollyBestilling.getSigrunstub());

                                   elasticBestilling.setSigrunPensjonsgivende(dollyBestilling.getSigrunstubPensjonsgivende());

                                   elasticBestilling.setArenaBruker(nonNull(dollyBestilling.getArenaforvalter()) ?
                                           mapperFacade.map(dollyBestilling.getArenaforvalter(), ElasticBestilling.ArenaBruker.class) : null);
                                   elasticBestilling.setArenaAap(nonNull(dollyBestilling.getArenaforvalter()) ?
                                           dollyBestilling.getArenaforvalter().getAap() : null);

                                   elasticBestilling.setArenaAap115(nonNull(dollyBestilling.getArenaforvalter()) ?
                                           dollyBestilling.getArenaforvalter().getAap115() : null);

                                   elasticBestilling.setArenaDagpenger(nonNull(dollyBestilling.getArenaforvalter()) ?
                                           dollyBestilling.getArenaforvalter().getDagpenger() : null);
                               }
                           }
                ).byDefault()
                .register();


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

                                   } catch (JsonProcessingException | IllegalArgumentException e) {

                                       elasticBestilling.setIgnore(true);
                                       log.warn("Kunne ikke konvertere fra JSON for bestilling-ID={}", bestilling.getId());

                                   } finally {

                                       elasticBestilling.setId(bestilling.getId());
                                   }
                               }
                           }
                )
                .register();
    }
}