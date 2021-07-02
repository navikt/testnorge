package no.nav.pdl.forvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.PostnummerService;
import no.nav.registre.testnorge.libs.dto.adresseservice.v1.VegadresseDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@RequiredArgsConstructor
public class KontaktInformasjonForDoedsboMappingStrategy implements MappingStrategy {

    private static final String LANDKODE_NORGE = "NOR";
    private static final String ADRESSE_TYPE_NOT_SUPPORTED = "KontaktinformasjonForDoedsbo: Ukjent bosted er ikke støttet";
    private final PostnummerService postnummerService;

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(BostedadresseDTO.class, KontaktinformasjonForDoedsboDTO.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(BostedadresseDTO kilde, KontaktinformasjonForDoedsboDTO destinasjon, MappingContext context) {

                        if (nonNull(kilde.getVegadresse())) {
                            destinasjon.setAdresselinje1(format("%s %s", kilde.getVegadresse().getAdressenavn(),
                                    kilde.getVegadresse().getHusnummer()));
                            destinasjon.setPostnummer(kilde.getVegadresse().getPostnummer());
                            destinasjon.setPoststedsnavn(postnummerService.getNavn(kilde.getVegadresse().getPostnummer()));
                            destinasjon.setLandkode(LANDKODE_NORGE);

                        } else if (nonNull(kilde.getUtenlandskAdresse())) {
                            destinasjon.setAdresselinje1(kilde.getUtenlandskAdresse().getAdressenavnNummer());
                            destinasjon.setAdresselinje2(format("%s %s",
                                    blankCheck(kilde.getUtenlandskAdresse().getBySted(), ""),
                                    blankCheck(kilde.getUtenlandskAdresse().getRegion(), "")));
                            destinasjon.setPostnummer(kilde.getUtenlandskAdresse().getPostkode());
                            destinasjon.setLandkode(kilde.getUtenlandskAdresse().getLandkode());

                        } else if (nonNull(kilde.getMatrikkeladresse())) {
                            destinasjon.setAdresselinje1(kilde.getMatrikkeladresse().getAdressetilleggsnavn());
                            destinasjon.setAdresselinje2(format("Gårdsnummer: %s, bruksnummer: %s",
                                    kilde.getMatrikkeladresse().getGaardsnummer(),
                                    kilde.getMatrikkeladresse().getBruksnummer()));
                            destinasjon.setPostnummer(kilde.getMatrikkeladresse().getPostnummer());
                            destinasjon.setPoststedsnavn(postnummerService.getNavn(kilde.getMatrikkeladresse().getPostnummer()));
                            destinasjon.setLandkode(LANDKODE_NORGE);

                        } else {

                            throw new InvalidRequestException(ADRESSE_TYPE_NOT_SUPPORTED);
                        }
                    }
                })
                .register();

        factory.classMap(VegadresseDTO.class, KontaktinformasjonForDoedsboDTO.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(VegadresseDTO kilde, KontaktinformasjonForDoedsboDTO destinasjon, MappingContext context) {

                        destinasjon.setAdresselinje1(format("%s %d", kilde.getAdressenavn(), kilde.getHusnummer()));
                        destinasjon.setPostnummer(kilde.getPostnummer());
                        destinasjon.setPoststedsnavn(kilde.getPoststed());
                        destinasjon.setLandkode(LANDKODE_NORGE);
                    }
                })
                .register();
    }
}