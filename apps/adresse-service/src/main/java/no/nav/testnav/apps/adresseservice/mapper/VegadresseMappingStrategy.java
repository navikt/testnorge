package no.nav.testnav.apps.adresseservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class VegadresseMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(no.nav.testnav.apps.adresseservice.dto.VegadresseDTO.class, VegadresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(no.nav.testnav.apps.adresseservice.dto.VegadresseDTO kilde, VegadresseDTO destinasjon, MappingContext context) {

                        if (nonNull(kilde.getVegadresse())) {
                            destinasjon.setMatrikkelId(kilde.getVegadresse().getId());
                            destinasjon.setHusnummer(kilde.getVegadresse().getNummer());
                            destinasjon.setHusbokstav(kilde.getVegadresse().getBokstav());
                            destinasjon.setTilleggsnavn(kilde.getVegadresse().getAdressetilleggsnavn());

                            if (nonNull(kilde.getVegadresse().getVeg())) {
                                destinasjon.setAdressenavn(kilde.getVegadresse().getVeg().getAdressenavn());
                                destinasjon.setAdressekode(kilde.getVegadresse().getVeg().getAdressekode());

                                if (nonNull(kilde.getVegadresse().getVeg().getKommune())) {
                                    destinasjon.setKommunenummer(kilde.getVegadresse().getVeg().getKommune().getKommunenummer());
                                    destinasjon.setKommunenavn(kilde.getVegadresse().getVeg().getKommune().getKommunenavn());

                                    if (nonNull(kilde.getVegadresse().getVeg().getKommune().getFylke())) {
                                        destinasjon.setFylkesnummer(kilde.getVegadresse().getVeg().getKommune().getFylke().getFylkesnummer());
                                        destinasjon.setFylkesnavn(kilde.getVegadresse().getVeg().getKommune().getFylke().getFylkesnavn());
                                    }
                                }
                            }

                            if (nonNull(kilde.getVegadresse().getBydel())) {
                                destinasjon.setBydelsnummer(kilde.getVegadresse().getBydel().getBydelsnummer());
                                destinasjon.setBydelsnavn(kilde.getVegadresse().getBydel().getBydelsnavn());
                            }

                            if (nonNull(kilde.getVegadresse().getPostnummeromraade())) {
                                destinasjon.setPostnummer(kilde.getVegadresse().getPostnummeromraade().getPostnummer());
                                destinasjon.setPoststed(kilde.getVegadresse().getPostnummeromraade().getPoststed());
                            }
                        }
                    }
                }).register();
    }
}
