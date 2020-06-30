package no.nav.dolly.bestilling.sykemelding.mapper;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class SyntSykemeldingMapperStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsSykemelding.RsSyntSykemelding.class, SyntSykemeldingRequest.class)
                .customize(new CustomMapper<RsSykemelding.RsSyntSykemelding, SyntSykemeldingRequest>() {
                    @Override
                    public void mapAtoB(RsSykemelding.RsSyntSykemelding sykemelding, SyntSykemeldingRequest request, MappingContext context) {

                    }
                })
                .byDefault()
                .register();
    }
}
