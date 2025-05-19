package no.nav.dolly.bestilling.etterlatte.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.etterlatte.dto.VedtakRequestDTO;
import no.nav.dolly.domain.resultset.etterlatte.EtterlatteYtelse;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class EtterlatteVedtakMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(VedtakRequestDTO.class, VedtakRequestDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(VedtakRequestDTO kilde, VedtakRequestDTO destinasjon, MappingContext context) {

                        var etterlatteYtelse = (EtterlatteYtelse) context.getProperty("etterlattYtelse");
                        destinasjon.setType(etterlatteYtelse.getYtelse());
                        destinasjon.setSoeker(etterlatteYtelse.getSoeker());
                    }
                })
                .byDefault()
                .register();
    }
}
