package no.nav.testnav.apps.tenorsearchservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktResponse;
import no.nav.testnav.apps.tenorsearchservice.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class TenorOversiktFilterMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {
        factory.classMap(TenorOversiktResponse.class, TenorOversiktResponse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TenorOversiktResponse response, TenorOversiktResponse response2, MappingContext context) {
                    }
                })
                .byDefault()
                .register();

        factory.classMap(TenorOversiktResponse.Data.class, TenorOversiktResponse.Data.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TenorOversiktResponse.Data input, TenorOversiktResponse.Data output, MappingContext context) {

                        var eksister = context.getProperty()
                    }
                })
                .exclude("personer")
                .byDefault()
                .register();
    }
}
