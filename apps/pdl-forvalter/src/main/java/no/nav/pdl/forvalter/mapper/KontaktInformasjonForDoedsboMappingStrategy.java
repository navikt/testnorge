package no.nav.pdl.forvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.domain.PdlBostedadresse;
import no.nav.pdl.forvalter.domain.PdlKontaktinformasjonForDoedsbo;
import no.nav.pdl.forvalter.utils.PostnummerService;
import no.nav.registre.testnorge.libs.dto.adresseservice.v1.VegadresseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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

        factory.classMap(PdlBostedadresse.class, PdlKontaktinformasjonForDoedsbo.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(PdlBostedadresse kilde, PdlKontaktinformasjonForDoedsbo destinasjon, MappingContext context) {

                        if (nonNull(kilde.getVegadresse())) {
                            destinasjon.setAdresselinje1(kilde.getVegadresse().getAdressenavn());
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

                            throw new HttpClientErrorException(BAD_REQUEST, ADRESSE_TYPE_NOT_SUPPORTED);
                        }
                    }
                })
                .register();

        factory.classMap(VegadresseDTO.class, PdlKontaktinformasjonForDoedsbo.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(VegadresseDTO kilde, PdlKontaktinformasjonForDoedsbo destinasjon, MappingContext context) {

                       destinasjon.setAdresselinje1(kilde.getAdressenavn());
                       destinasjon.setPostnummer(kilde.getPostnummer());
                       destinasjon.setPoststedsnavn(kilde.getPoststed());
                       destinasjon.setLandkode(LANDKODE_NORGE);
                    }
                })
                .register();
    }
}