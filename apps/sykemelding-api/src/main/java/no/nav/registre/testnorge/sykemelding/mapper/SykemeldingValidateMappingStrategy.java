package no.nav.registre.testnorge.sykemelding.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;
import no.nav.registre.testnorge.sykemelding.dto.ReceivedSykemeldingDTO;

public class SykemeldingValidateMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Sykemelding.class, ReceivedSykemeldingDTO.class)
                .customize(new CustomMapper<Sykemelding, ReceivedSykemeldingDTO>() {
                    @Override
                    public void mapAtoB(Sykemelding sykemelding, ReceivedSykemeldingDTO receivedSykemeldingDTO, MappingContext context) {
                        // TBD
                    }
                })
                .byDefault()
                .register();

    }
}
