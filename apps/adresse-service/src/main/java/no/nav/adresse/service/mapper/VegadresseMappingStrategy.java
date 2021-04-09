package no.nav.adresse.service.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.adresse.service.dto.PdlAdresseResponse.Hits;
import no.nav.adresse.service.dto.PdlAdresseResponse.Vegadresse;
import org.springframework.stereotype.Component;

@Component
public class VegadresseMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Hits.class, Vegadresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Hits hits, Vegadresse vegadresse, MappingContext context) {

                        vegadresse.setMatrikkelId(hits.getVegadresse().getMatrikkelId());
                        vegadresse.setAdressenavn(hits.getVegadresse().getAdressenavn());
                        vegadresse.setHusnummer(hits.getVegadresse().getHusnummer());
                        vegadresse.setHusbokstav(hits.getVegadresse().getHusbokstav());
                        vegadresse.setPostnummer(hits.getVegadresse().getPostnummer());
                        vegadresse.setPoststed(hits.getVegadresse().getPoststed());
                        vegadresse.setKommunenummer(hits.getVegadresse().getKommunenummer());
                        vegadresse.setKommunenavn(hits.getVegadresse().getKommunenavn());
                        vegadresse.setBydelsnummer(hits.getVegadresse().getBydelsnummer());
                        vegadresse.setBydelsnavn(hits.getVegadresse().getBydelsnavn());
                        vegadresse.setTilleggsnavn(hits.getVegadresse().getTilleggsnavn());
                    }
                }).register();
    }
}
