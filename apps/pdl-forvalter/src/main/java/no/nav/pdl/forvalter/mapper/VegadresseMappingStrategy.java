package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.MapperFactory;
import org.springframework.stereotype.Component;

@Component
public class VegadresseMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

//        factory.classMap(Hits.class, Vegadresse.class)
//                .customize(new CustomMapper<>() {
//                    @Override
//                    public void mapAtoB(Hits hits, Vegadresse vegadresse, MappingContext context) {
//
//                        vegadresse.setMatrikkelId(hits.getVegadresse().getMatrikkelId());
//                        vegadresse.setAdressenavn(hits.getVegadresse().getAdressenavn());
//                        vegadresse.setHusnummer(hits.getVegadresse().getHusnummer());
//                        vegadresse.setHusbokstav(hits.getVegadresse().getHusbokstav());
//                        vegadresse.setPostnummer(hits.getVegadresse().getPostnummer());
//                        vegadresse.setPoststed(hits.getVegadresse().getPoststed());
//                        vegadresse.setKommunenummer(hits.getVegadresse().getKommunenummer());
//                        vegadresse.setKommunenavn(hits.getVegadresse().getKommunenavn());
//                        vegadresse.setBydelsnummer(hits.getVegadresse().getBydelsnummer());
//                        vegadresse.setBydelsnavn(hits.getVegadresse().getBydelsnavn());
//                        vegadresse.setTilleggsnavn(hits.getVegadresse().getTilleggsnavn());
//                    }
//                }).register();
    }
}
