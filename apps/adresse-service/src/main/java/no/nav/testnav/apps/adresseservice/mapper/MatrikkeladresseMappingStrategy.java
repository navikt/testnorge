package no.nav.testnav.apps.adresseservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class MatrikkeladresseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseDTO.class, MatrikkeladresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseDTO kilde, MatrikkeladresseDTO destinasjon, MappingContext context) {

                        if (nonNull(kilde.getMatrikkeladresse())) {

                            destinasjon.setMatrikkelId(kilde.getMatrikkeladresse().getId());
                            destinasjon.setTilleggsnavn(kilde.getMatrikkeladresse().getAdressetilleggsnavn());

                            if (nonNull(kilde.getMatrikkeladresse().getPostnummeromraade())) {
                                destinasjon.setPostnummer(kilde.getMatrikkeladresse().getPostnummeromraade().getPostnummer());
                                destinasjon.setPoststed(kilde.getMatrikkeladresse().getPostnummeromraade().getPoststed());
                            }

                            if (nonNull(kilde.getMatrikkeladresse().getMatrikkelenhet()) &&
                                    nonNull(kilde.getMatrikkeladresse().getMatrikkelenhet().getMatrikkelnummer())) {
                                destinasjon.setKommunenummer(kilde.getMatrikkeladresse().getMatrikkelenhet().getMatrikkelnummer().getKommunenummer());
                                destinasjon.setGaardsnummer(kilde.getMatrikkeladresse().getMatrikkelenhet().getMatrikkelnummer().getGardsnummer());
                                destinasjon.setBruksnummer(kilde.getMatrikkeladresse().getMatrikkelenhet().getMatrikkelnummer().getBruksnummer());
                            }
                        }
                    }
                }).register();
    }
}