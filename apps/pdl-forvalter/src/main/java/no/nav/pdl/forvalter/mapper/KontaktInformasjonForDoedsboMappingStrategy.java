package no.nav.pdl.forvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktinformasjonForDoedsboAdresse;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.CountryCodeMapperUtility.mapCountryCode;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@RequiredArgsConstructor
public class KontaktInformasjonForDoedsboMappingStrategy implements MappingStrategy {

    private static final String LANDKODE_NORGE = "NOR";
    private static final String ADRESSE_TYPE_NOT_SUPPORTED = "KontaktinformasjonForDoedsbo: Ukjent bosted er ikke støttet";
    private final KodeverkConsumer kodeverkConsumer;

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(BostedadresseDTO.class, KontaktinformasjonForDoedsboAdresse.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(BostedadresseDTO kilde, KontaktinformasjonForDoedsboAdresse destinasjon, MappingContext context) {

                        if (nonNull(kilde.getVegadresse())) {
                            destinasjon.setAdresselinje1(format("%s %s", kilde.getVegadresse().getAdressenavn(),
                                    kilde.getVegadresse().getHusnummer()));
                            destinasjon.setPostnummer(kilde.getVegadresse().getPostnummer());
                            destinasjon.setPoststedsnavn(kodeverkConsumer.getPoststedNavn(kilde.getVegadresse().getPostnummer()));
                            destinasjon.setLandkode(LANDKODE_NORGE);

                        } else if (nonNull(kilde.getUtenlandskAdresse())) {
                            destinasjon.setAdresselinje1(kilde.getUtenlandskAdresse().getAdressenavnNummer());
                            destinasjon.setAdresselinje2(format("%s %s",
                                    blankCheck(kilde.getUtenlandskAdresse().getBySted(), ""),
                                    blankCheck(kilde.getUtenlandskAdresse().getRegion(), "")));
                            destinasjon.setPostnummer(kilde.getUtenlandskAdresse().getPostkode());
                            destinasjon.setLandkode(kilde.getUtenlandskAdresse().getLandkode());

                        } else if (nonNull(kilde.getMatrikkeladresse())) {
                            destinasjon.setAdresselinje1(kilde.getMatrikkeladresse().getTilleggsnavn());
                            destinasjon.setAdresselinje2(format("Gårdsnummer: %s, bruksnummer: %s",
                                    kilde.getMatrikkeladresse().getGaardsnummer(),
                                    kilde.getMatrikkeladresse().getBruksnummer()));
                            destinasjon.setPostnummer(kilde.getMatrikkeladresse().getPostnummer());
                            destinasjon.setPoststedsnavn(kodeverkConsumer.getPoststedNavn(kilde.getMatrikkeladresse().getPostnummer()));
                            destinasjon.setLandkode(LANDKODE_NORGE);

                        } else {

                            throw new InvalidRequestException(ADRESSE_TYPE_NOT_SUPPORTED);
                        }
                    }
                })
                .register();

        factory.classMap(VegadresseDTO.class, KontaktinformasjonForDoedsboAdresse.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(VegadresseDTO kilde, KontaktinformasjonForDoedsboAdresse destinasjon, MappingContext context) {

                        destinasjon.setAdresselinje1(format("%s %d", kilde.getAdressenavn(), kilde.getHusnummer()));
                        destinasjon.setPostnummer(kilde.getPostnummer());
                        destinasjon.setPoststedsnavn(kilde.getPoststed());
                        destinasjon.setLandkode(LANDKODE_NORGE);
                    }
                })
                .register();

        factory.classMap(Map.class, KontaktinformasjonForDoedsboAdresse.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(Map kilde, KontaktinformasjonForDoedsboAdresse destinasjon, MappingContext context) {

                        var adresselinjer = (List<String>) kilde.get("adresselinjer");
                        destinasjon.setAdresselinje1(!adresselinjer.isEmpty() ? adresselinjer.getFirst() : "Ingen adresselinje funnet");
                        destinasjon.setAdresselinje2(adresselinjer.size() > 1 ? adresselinjer.get(1) : null);
                        destinasjon.setPostnummer((String) kilde.get("postnr"));

                        destinasjon.setPoststedsnavn(kodeverkConsumer.getPoststedNavn(destinasjon.getPostnummer()));
                        destinasjon.setLandkode(mapCountryCode((String) kilde.get("landkode")));
                    }
                })
                .register();

        factory.classMap(UtenlandskAdresseDTO.class, KontaktinformasjonForDoedsboAdresse.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(UtenlandskAdresseDTO kilde, KontaktinformasjonForDoedsboAdresse destinasjon, MappingContext context) {

                        destinasjon.setAdresselinje1(kilde.getAdressenavnNummer());
                        destinasjon.setAdresselinje2(format("%s %s",
                                blankCheck(kilde.getBySted(), ""),
                                blankCheck(kilde.getRegionDistriktOmraade(), "")));
                        destinasjon.setPostnummer(kilde.getPostkode());
                        destinasjon.setPoststedsnavn(kilde.getBySted());
                        destinasjon.setLandkode(kilde.getLandkode());
                    }
                })
                .register();
    }

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }
}