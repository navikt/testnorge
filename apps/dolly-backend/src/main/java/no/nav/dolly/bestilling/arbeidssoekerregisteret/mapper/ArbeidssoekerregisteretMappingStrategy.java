package no.nav.dolly.bestilling.arbeidssoekerregisteret.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssokerregisteretRequest;
import no.nav.dolly.domain.resultset.arbeidssoekerregistrering.RsArbeidssokerregisteret;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class ArbeidssoekerregisteretMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsArbeidssokerregisteret.class, ArbeidssokerregisteretRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsArbeidssokerregisteret rsArbeidssokerregisteret, ArbeidssokerregisteretRequest request, MappingContext context) {

                        var ident = (String) context.getProperty("ident");
                        request.setIdentitetsnummer(ident);
                    }
                })
                .byDefault()
                .register();
    }
}
