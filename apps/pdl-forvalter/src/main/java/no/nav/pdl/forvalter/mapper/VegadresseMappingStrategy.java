package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.PdlKontaktadresse.VegadresseForPost;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;

import org.springframework.stereotype.Component;

@Component
public class VegadresseMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.class, VegadresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO kildeAdresse,
                                        VegadresseDTO vegadresse, MappingContext context) {

                        vegadresse.setAdressetilleggsnavn(kildeAdresse.getTilleggsnavn());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.class, VegadresseForPost.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO kildeAdresse,
                                        VegadresseForPost vegadresse, MappingContext context) {

                        vegadresse.setAdressetilleggsnavn(kildeAdresse.getTilleggsnavn());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(VegadresseDTO.class, no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(VegadresseDTO kildeAdresse,
                                        no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO vegadresse, MappingContext context) {

                        vegadresse.setTilleggsnavn(kildeAdresse.getAdressetilleggsnavn());
                    }
                })
                .byDefault()
                .register();
    }
}