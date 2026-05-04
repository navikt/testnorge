package no.nav.dolly.bestilling.kelvinaap.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.kelvinaap.domain.AapOpprettRequest;
import no.nav.dolly.domain.resultset.kelvinaap.RsKelvinAapRequestDTO;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class KelvinAapMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsKelvinAapRequestDTO.class, AapOpprettRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsKelvinAapRequestDTO source, AapOpprettRequest destination, MappingContext context) {

                        destination.setIdent((String) context.getProperty("ident"));
                    }
                })
                .byDefault()
                .register();
    }
}