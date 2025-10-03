package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonsavtaleRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.Set;

import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.IDENT;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.MILJOER;

@Component
public class PensjonsavtaleMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PensjonData.Pensjonsavtale.class, PensjonsavtaleRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.Pensjonsavtale pensjonsavtale, PensjonsavtaleRequest pensjonsavtaleRequest, MappingContext context) {

                        var ident = (String) context.getProperty(IDENT);
                        var miljoer = (Set<String>) context.getProperty(MILJOER);

                        pensjonsavtaleRequest.setIdent(ident);
                        pensjonsavtaleRequest.setMiljoer(miljoer.stream().toList());
                    }
                })
                .byDefault()
                .register();
    }
}
