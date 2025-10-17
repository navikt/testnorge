package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpYtelseRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.Set;

import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.IDENT;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.MILJOER;

@Component
public class PensjonTpMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PensjonData.TpOrdning.class, PensjonTpForholdRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.TpOrdning tpOrdning, PensjonTpForholdRequest request, MappingContext context) {

                        request.setFnr((String) context.getProperty(IDENT));
                        request.setMiljoer((Set<String>) context.getProperty(MILJOER));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(PensjonData.TpYtelse.class, PensjonTpYtelseRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.TpYtelse tpYtelse, PensjonTpYtelseRequest request, MappingContext context) {

                        request.setFnr((String) context.getProperty(IDENT));
                        request.setMiljoer((Set<String>) context.getProperty(MILJOER));
                        request.setOrdning((String) context.getProperty("ordning"));

                        request.setYtelseType(tpYtelse.getType());
                    }
                })
                .byDefault()
                .register();
    }
}
