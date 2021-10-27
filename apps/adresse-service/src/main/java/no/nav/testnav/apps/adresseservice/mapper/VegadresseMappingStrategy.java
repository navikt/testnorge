package no.nav.testnav.apps.adresseservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.adresseservice.dto.PdlAdresseResponse;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import org.springframework.stereotype.Component;

@Component
public class VegadresseMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PdlAdresseResponse.Hits.class, VegadresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PdlAdresseResponse.Hits hits, VegadresseDTO vegadresse, MappingContext context) {

                        vegadresse.setMatrikkelId(hits.getVegadresse().getMatrikkelId());
                        vegadresse.setAdressekode(hits.getVegadresse().getAdressekode());
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
                        vegadresse.setFylkesnummer(hits.getVegadresse().getFylkesnummer());
                        vegadresse.setFylkesnavn(hits.getVegadresse().getFylkesnavn());
                    }
                }).register();
    }
}
