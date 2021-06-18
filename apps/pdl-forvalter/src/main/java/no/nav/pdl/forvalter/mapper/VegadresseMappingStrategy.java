package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.domain.VegadresseDTO;
import no.nav.pdl.forvalter.dto.PdlAdresseResponse.Vegadresse;
import no.nav.pdl.forvalter.dto.PdlKontaktadresse.VegadresseForPost;
import org.springframework.stereotype.Component;

@Component
public class VegadresseMappingStrategy implements MappingStrategy {



    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Vegadresse.class, VegadresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Vegadresse kildeAdresse, VegadresseDTO vegadresse, MappingContext context) {

                        vegadresse.setAdressetilleggsnavn(kildeAdresse.getTilleggsnavn());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Vegadresse.class, VegadresseForPost.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Vegadresse kildeAdresse, VegadresseForPost vegadresse, MappingContext context) {

                        vegadresse.setAdressetilleggsnavn(kildeAdresse.getTilleggsnavn());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(VegadresseDTO.class, Vegadresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(VegadresseDTO kildeAdresse, Vegadresse vegadresse, MappingContext context) {

                        vegadresse.setTilleggsnavn(kildeAdresse.getAdressetilleggsnavn());
                    }
                })
                .byDefault()
                .register();
    }
}