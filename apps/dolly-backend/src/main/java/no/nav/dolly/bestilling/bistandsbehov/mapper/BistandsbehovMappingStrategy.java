package no.nav.dolly.bestilling.bistandsbehov.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.bistandsbehov.dto.BistandVedtakRequestDTO;
import no.nav.dolly.domain.resultset.bistandsbehov.RsBistandsbehovDTO;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class BistandsbehovMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsBistandsbehovDTO.class, BistandVedtakRequestDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsBistandsbehovDTO kilde, BistandVedtakRequestDTO destinasjon, MappingContext context) {

                        destinasjon.setFnr((String) context.getProperty("ident"));
                        destinasjon.setOppfolgingsEnhet((String) context.getProperty("norgEnhet"));
                    }
                })
                .byDefault()
                .register();
    }
}
