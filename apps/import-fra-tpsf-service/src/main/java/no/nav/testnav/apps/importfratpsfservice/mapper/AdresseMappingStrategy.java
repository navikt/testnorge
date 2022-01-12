package no.nav.testnav.apps.importfratpsfservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmeldingTrans1;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static no.nav.testnav.apps.importfratpsfservice.mapper.HusbokstavDekoder.decode;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

@Component
public class AdresseMappingStrategy implements MappingStrategy {

    private static final String GATE_ADRESSE = "O";
    private static final String MATRIKKEL_ADRESSE = "M";
    private static final String UTEN_FAST_BOSTED = "5";
    private static final String EMPTY_VAL = "00000000";
    private static final String NORGE = "000";

    private static Integer toNumeric(String number) {
        return isNotBlank(number) && isNumeric(number) ?
                Integer.decode(number.replaceFirst("^0+(?!$)", "")) : null;
    }

    private static LocalDateTime getDate(String dato) {
        return isNotBlank(dato) && !EMPTY_VAL.equals(dato) ?
                LocalDate.parse(dato, DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay() : null;
    }

    private static List<String> getAdresselinjer(SkdEndringsmeldingTrans1 adresse) {

        var adresseLinjer = new ArrayList<String>();
        if (isNotBlank(adresse.getAdresse1())) {
            adresseLinjer.add(adresse.getAdresse1());
        }
        if (isNotBlank(adresse.getAdresse2())) {
            adresseLinjer.add(adresse.getAdresse2());
        }
        if (isNotBlank(adresse.getAdresse3())) {
            adresseLinjer.add(adresse.getAdresse3());
        }
        return adresseLinjer;
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(SkdEndringsmeldingTrans1.class, BostedadresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkdEndringsmeldingTrans1 source, BostedadresseDTO target, MappingContext context) {

                        if (GATE_ADRESSE.equalsIgnoreCase(source.getAdressetype())) {
                            target.setVegadresse(VegadresseDTO.builder()
                                    .adressenavn(source.getAdressenavn())
                                    .adressekode(source.getGateGaard())
                                    .husnummer(source.getHusBruk().replaceFirst("^0+(?!$)", ""))
                                    .husbokstav(decode(source.getBokstavFestenr()))
                                    .bruksenhetsnummer(source.getBolignr())
                                    .kommunenummer(source.getKommunenummer())
                                    .postnummer(source.getPostnummer())
                                    .tilleggsnavn(source.getTilleggsadresse())
                                    .build());
                        } else if (MATRIKKEL_ADRESSE.equalsIgnoreCase(source.getAdressetype())) {
                            target.setMatrikkeladresse(MatrikkeladresseDTO.builder()
                                    .gaardsnummer(toNumeric(source.getGateGaard()))
                                    .bruksnummer(toNumeric(source.getHusBruk()))
                                    .kommunenummer(source.getKommunenummer())
                                    .postnummer(source.getPostnummer())
                                    .tilleggsnavn(source.getTilleggsadresse())
                                    .bruksenhetsnummer(source.getBolignr())
                                    .build());
                        } else if (UTEN_FAST_BOSTED.equals(source.getSpesRegType())) {
                            target.setUkjentBosted(BostedadresseDTO.UkjentBostedDTO.builder()
                                    .bostedskommune(source.getKommunenummer())
                                    .build());
                        }
                        target.setAngittFlyttedato(getDate(source.getFlyttedatoAdr()));
                    }
                })
                .register();

        factory.classMap(SkdEndringsmeldingTrans1.class, KontaktadresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkdEndringsmeldingTrans1 source, KontaktadresseDTO target, MappingContext context) {

                        if (isBlank(source.getPostadrLand()) || NORGE.equals(source.getPostadrLand())) {
                            target.setPostadresseIFrittFormat(KontaktadresseDTO.PostadresseIFrittFormat.builder()
                                    .adresselinjer(getAdresselinjer(source))
                                    .build());
                        }

                        if (isNotBlank(source.getPostadrLand()) && !NORGE.equals(source.getPostadrLand())) {
                            target.setUtenlandskAdresseIFrittFormat(KontaktadresseDTO.UtenlandskAdresseIFrittFormat.builder()
                                    .adresselinjer(getAdresselinjer(source))
                                    .landkode(LandkodeDekoder.convert(source.getPostadrLand()))
                                    .build());
                        }
                    }
                })
                .register();
    }
}
