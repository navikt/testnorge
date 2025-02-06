package no.nav.dolly.bestilling.arbeidssoekerregisteret.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssoekerregisteretRequest;
import no.nav.dolly.domain.resultset.arbeidssoekerregistrering.RsArbeidssoekerregisteret;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class ArbeidssoekerregisteretMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsArbeidssoekerregisteret.class, ArbeidssoekerregisteretRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsArbeidssoekerregisteret rsArbeidssokerregisteret, ArbeidssoekerregisteretRequest request, MappingContext context) {

                        var ident = (String) context.getProperty("ident");
                        request.setIdentitetsnummer(ident);
                    }
                })
                .byDefault()
                .register();
    }
}
