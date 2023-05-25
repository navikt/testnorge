package no.nav.dolly.bestilling.medl.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.medl.MedlData;
import no.nav.dolly.domain.resultset.medl.RsMedl;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class MedlMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsMedl.class, MedlData.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsMedl rsMedl, MedlData medlDataRequest, MappingContext context) {

                        medlDataRequest.setIdent((String) context.getProperty("ident"));
                        if (!isNull(rsMedl.getStudieinformasjon())) {
                            medlDataRequest.setStudieinformasjon(MedlData.Studieinformasjon.builder()
                                    .delstudie(rsMedl.getStudieinformasjon().getDelstudie())
                                    .soeknadInnvilget(rsMedl.getStudieinformasjon().getSoeknadInnvilget())
                                    .studieland(rsMedl.getStudieinformasjon().getStudieland())
                                    .statsborgerland(rsMedl.getStudieinformasjon().getStudieland())
                                    .build());
                        }
                    }
                })
                .exclude("studieinformasjon")
                .byDefault()
                .register();
    }
}