package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PensjonAlderspensjonMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PensjonData.Alderspensjon.class, AlderspensjonRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.Alderspensjon tpOrdning, AlderspensjonRequest request, MappingContext context) {

                        request.setPid((String) context.getProperty("ident"));
                        request.setMiljoer((List<String>) context.getProperty("miljoer"));
                        request.setStatsborgerskap("NOR");
                    }
                })
                .byDefault()
                .register();
    }
}