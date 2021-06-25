package no.nav.registre.testnorge.arbeidsforholdservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.FartoeyDTO;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class ArbeidsforholdResponseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(ArbeidsforholdDTO.class,
                no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.ArbeidsforholdDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(ArbeidsforholdDTO arbeidsforholdResponse, no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.ArbeidsforholdDTO arbeidsforhold, MappingContext context) {

                        if (nonNull(arbeidsforholdResponse.getArbeidsavtaler()) && arbeidsforholdResponse.getArbeidsavtaler().isEmpty()) {
                            arbeidsforhold.setFartoey(FartoeyDTO.builder()
                                    .skipsregister(arbeidsforholdResponse.getArbeidsavtaler().get(0).getSkipsregister())
                                    .skipstype(arbeidsforholdResponse.getArbeidsavtaler().get(0).getSkipstype())
                                    .fartsomraade(arbeidsforholdResponse.getArbeidsavtaler().get(0).getFartsomraade())
                                    .build());
                        }
                    }
                })
                .byDefault()
                .register();
    }

}