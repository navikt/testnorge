package no.nav.adresse.service.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.adresse.service.dto.MatrikkelAdresseResponse;
import no.nav.adresse.service.dto.MatrikkeladresseDTO;
import org.springframework.stereotype.Component;

@Component
public class MatrikkeladresseMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(MatrikkelAdresseResponse.Hits.class, MatrikkeladresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(MatrikkelAdresseResponse.Hits hits, MatrikkeladresseDTO vegadresse, MappingContext context) {

                        vegadresse.setMatrikkelId(hits.getMatrikkeladresse().getMatrikkelId());
                        vegadresse.setPostnummer(hits.getMatrikkeladresse().getPostnummer());
                        vegadresse.setPoststed(hits.getMatrikkeladresse().getPoststed());
                        vegadresse.setKommunenummer(hits.getMatrikkeladresse().getKommunenummer());
                        vegadresse.setTilleggsnavn(hits.getMatrikkeladresse().getTilleggsnavn());
                        vegadresse.setGardsnummer(hits.getMatrikkeladresse().getGardsnummer());
                        vegadresse.setGardsnummer(hits.getMatrikkeladresse().getGardsnummer());
                        vegadresse.setBruksnummer(hits.getMatrikkeladresse().getBruksnummer());
                    }
                }).register();
    }
}
