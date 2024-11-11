package no.nav.testnav.altinn3tilgangservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.BrregResponseDTO;
import no.nav.testnav.altinn3tilgangservice.domain.Organisasjon;
import org.springframework.stereotype.Component;

@Component
public class BrrregResponseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(BrregResponseDTO.class, Organisasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BrregResponseDTO kilde, Organisasjon destianasjon, MappingContext context) {

                        destianasjon.setWebUrl(kilde.get_links().getSelf().getHref());
                        mapperFacade.map(kilde.get_embedded().getEnheter().getFirst(), destianasjon, context);
                    }
                }).byDefault()
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
