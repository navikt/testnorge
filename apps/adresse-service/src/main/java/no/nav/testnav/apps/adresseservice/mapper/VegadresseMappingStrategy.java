package no.nav.testnav.apps.adresseservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.adresseservice.dto.PdlAdresseResponse;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

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

        factory.classMap(no.nav.testnav.apps.adresseservice.dto.VegadresseDTO.class, VegadresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(no.nav.testnav.apps.adresseservice.dto.VegadresseDTO kilde, VegadresseDTO destinasjon, MappingContext context) {

                        destinasjon.setMatrikkelId(kilde.getVegadresse().getId());
                        destinasjon.setAdressekode(kilde.getVegadresse().getVeg().getAdressekode());
                        destinasjon.setAdressenavn(kilde.getVegadresse().getVeg().getAdressenavn());
                        destinasjon.setHusnummer(kilde.getVegadresse().getNummer());
                        destinasjon.setHusbokstav(kilde.getVegadresse().getBokstav());
                        destinasjon.setPostnummer(kilde.getVegadresse().getPostnummeromraade().getPostnummer());
                        destinasjon.setPoststed(kilde.getVegadresse().getPostnummeromraade().getPoststed());
                        destinasjon.setKommunenummer(kilde.getVegadresse().getVeg().getKommune().getKommunenummer());
                        destinasjon.setKommunenavn(kilde.getVegadresse().getVeg().getKommune().getKommunenavn());
                        if (nonNull(kilde.getVegadresse().getBydel())) {
                            destinasjon.setBydelsnummer(kilde.getVegadresse().getBydel().getBydelsnummer());
                            destinasjon.setBydelsnavn(kilde.getVegadresse().getBydel().getBydelsnavn());
                        }
                        destinasjon.setTilleggsnavn(kilde.getVegadresse().getAdressetilleggsnavn());
                        destinasjon.setFylkesnummer(kilde.getVegadresse().getVeg().getKommune().getFylke().getFylkesnummer());
                        destinasjon.setFylkesnavn(kilde.getVegadresse().getVeg().getKommune().getFylke().getFylkesnavn());
                    }
                }).register();
    }
}
