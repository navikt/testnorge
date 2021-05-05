package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.domain.PdlKontaktadresse;
import no.nav.pdl.forvalter.domain.PdlVegadresse;
import no.nav.pdl.forvalter.dto.PdlAdresseResponse;
import org.springframework.stereotype.Component;

@Component
public class VegadresseMappingStrategy implements MappingStrategy {



    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PdlAdresseResponse.Vegadresse.class, PdlVegadresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PdlAdresseResponse.Vegadresse kildeAdresse, PdlVegadresse vegadresse, MappingContext context) {

                        vegadresse.setAdressetilleggsnavn(kildeAdresse.getTilleggsnavn());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(PdlAdresseResponse.Vegadresse.class, PdlKontaktadresse.VegadresseForPost.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PdlAdresseResponse.Vegadresse kildeAdresse, PdlKontaktadresse.VegadresseForPost vegadresse, MappingContext context) {

                        vegadresse.setAdressetilleggsnavn(kildeAdresse.getTilleggsnavn());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(PdlVegadresse.class, PdlAdresseResponse.Vegadresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PdlVegadresse kildeAdresse, PdlAdresseResponse.Vegadresse vegadresse, MappingContext context) {

                        vegadresse.setTilleggsnavn(kildeAdresse.getAdressetilleggsnavn());
                    }
                })
                .byDefault()
                .register();
    }
}