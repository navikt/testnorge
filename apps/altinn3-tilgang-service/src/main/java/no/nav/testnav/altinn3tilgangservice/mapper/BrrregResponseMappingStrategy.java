package no.nav.testnav.altinn3tilgangservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.BrregResponseDTO;
import no.nav.testnav.altinn3tilgangservice.domain.Organisasjon;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class BrrregResponseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(BrregResponseDTO.class, Organisasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BrregResponseDTO kilde, Organisasjon destinasjon, MappingContext context) {

                        if (isNull(kilde.getFeilmelding())) {
                            var link = kilde.get_links().getSelf().getHref();
                            if (nonNull(kilde.get_embedded())) {
                                mapperFacade.map(kilde.get_embedded().getEnheter().getFirst(), destinasjon, context);
                            } else {
                                destinasjon.setNavn("Ukjent organisasjon hos BRRREG");
                                destinasjon.setOrganisasjonsform("???");
                                destinasjon.setOrganisasjonsnummer(link.substring(link.indexOf('=') + 1));
                            }
                            destinasjon.setUrl(link);
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(BrregResponseDTO.Enhet.class, Organisasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BrregResponseDTO.Enhet kilde, Organisasjon destianasjon, MappingContext context) {

                        destianasjon.setOrganisasjonsform(kilde.getOrganisasjonsform().getKode());
                    }
                }).byDefault()
                .register();
    }
}
