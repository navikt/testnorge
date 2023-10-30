package no.nav.dolly.elastic.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class ElasticBestillingStrategyMapping implements MappingStrategy {

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
    }
}
