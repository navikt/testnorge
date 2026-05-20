package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.OrganisasjonAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktinformasjonForDoedsboAdresse;
import org.springframework.stereotype.Component;

@Component
public class OrganisasjonAdresseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(OrganisasjonAdresseDTO.class, KontaktinformasjonForDoedsboAdresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(OrganisasjonAdresseDTO kilde, KontaktinformasjonForDoedsboAdresse dest, MappingContext context) {

                        if (!kilde.getAdresselinjer().isEmpty()) {
                            dest.setAdresselinje1(kilde.getAdresselinjer().get(0));
                        }
                        if (kilde.getAdresselinjer().size() > 1) {
                            dest.setAdresselinje2(kilde.getAdresselinjer().get(1));
                        }
                        dest.setPostnummer(kilde.getPostnr());
                        dest.setPoststedsnavn(kilde.getPoststed());
                    }
                })
                .byDefault()
                .register();
    }
}
