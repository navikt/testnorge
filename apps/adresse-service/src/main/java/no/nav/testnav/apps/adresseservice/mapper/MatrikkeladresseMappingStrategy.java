package no.nav.testnav.apps.adresseservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.adresseservice.dto.PdlAdresseResponse;
import no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO;
import org.springframework.stereotype.Component;

@Component
public class MatrikkeladresseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PdlAdresseResponse.Hits.class, MatrikkeladresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PdlAdresseResponse.Hits hits, MatrikkeladresseDTO vegadresse, MappingContext context) {

                        vegadresse.setMatrikkelId(hits.getMatrikkeladresse().getMatrikkelId());
                        vegadresse.setPostnummer(hits.getMatrikkeladresse().getPostnummer());
                        vegadresse.setPoststed(hits.getMatrikkeladresse().getPoststed());
                        vegadresse.setKommunenummer(hits.getMatrikkeladresse().getKommunenummer());
                        vegadresse.setTilleggsnavn(hits.getMatrikkeladresse().getTilleggsnavn());
                        vegadresse.setGaardsnummer(hits.getMatrikkeladresse().getGaardsnummer());
                        vegadresse.setBruksnummer(hits.getMatrikkeladresse().getBruksnummer());
                    }
                }).register();
    }
}
