package no.nav.testnav.apps.importfratpsfservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmeldingTrans1;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static no.nav.testnav.apps.importfratpsfservice.mapper.HusbokstavDekoder.getHusbokstav;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

@Component
public class AdresseMappingStrategy implements MappingStrategy {

    private static final String GATE_ADRESSE = "O";
    private static final String MATRIKKEL_ADRESSE = "M";
    private static final String UTEN_FAST_BOSTED = "5";

    private static Integer toNumeric(String number) {
        return isNotBlank(number) && isNumeric(number) ? Integer.getInteger(number) : null;
    }

    private static LocalDateTime getDate(String dato) {
        return isNotBlank(dato) ? LocalDate.parse(dato).atStartOfDay() : null;
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
                                    .husnummer(source.getHusBruk())
                                    .husbokstav(getHusbokstav(source.getBokstavFestenr()))
                                    .bruksenhetsnummer(source.getBolignr())
                                    .kommunenummer(source.getKommunenummer())
                                    .postnummer(source.getPostnummer())
                                    .tilleggsnavn(source.getTilleggsadresse())
                                    .build());
                        } else if (MATRIKKEL_ADRESSE.equalsIgnoreCase(source.getAdressetype())) {
                            target.setMatrikkeladresse(MatrikkeladresseDTO.builder()
                                    .gaardsnummer(toNumeric(source.getGateGaard()))
                                    .bruksenhetsnummer(source.getHusBruk())
                                    .kommunenummer(source.getKommunenummer())
                                    .postnummer(source.getPostnummer())
                                    .tilleggsnavn(source.getTilleggsadresse())
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
    }
}
