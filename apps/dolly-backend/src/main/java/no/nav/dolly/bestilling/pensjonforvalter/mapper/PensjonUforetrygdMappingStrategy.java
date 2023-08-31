package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonUforetrygdRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PensjonUforetrygdMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PensjonData.Uforetrygd.class, PensjonUforetrygdRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.Uforetrygd uforetrygd, PensjonUforetrygdRequest pensjonUforetrygdRequest, MappingContext context) {

                        var ident = (String) context.getProperty("ident");
                        var miljoer = (List<String>) context.getProperty("miljoer");

                        pensjonUforetrygdRequest.setFnr(ident);
                        pensjonUforetrygdRequest.setMiljoer(miljoer);
                    }
                })
                .byDefault()
                .register();
    }
}
