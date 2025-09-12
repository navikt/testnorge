package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AfpOffentligRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.IDENT;

@Component
public class PensjonAfpOffentligMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PensjonData.AfpOffentlig.class, AfpOffentligRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.AfpOffentlig kilde, AfpOffentligRequest target, MappingContext context) {

                        var ident = (String) context.getProperty(IDENT);
                        target.getMocksvar()
                                .forEach(mocksvar -> mocksvar.setFnr(ident));
                    }
                })
                .byDefault()
                .register();
    }
}
