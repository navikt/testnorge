package no.nav.testnav.apps.tpsmessagingservice.service.skd;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.SkdMeldingTrans1;
import no.nav.testnav.apps.tpsmessagingservice.utils.ConvertDateToStringUtility;
import no.nav.testnav.apps.tpsmessagingservice.utils.HentDatoFraIdentUtility;
import no.nav.testnav.apps.tpsmessagingservice.utils.HusbokstavEncoder;
import no.nav.testnav.apps.tpsmessagingservice.utils.NullcheckUtil;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VegadresseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static no.nav.testnav.apps.tpsmessagingservice.utils.HentDatoFraIdentUtility.enforceValidTpsDate;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class AdresseAppenderService {

    private static final Pattern HUSNUMMER_PATTERN = Pattern.compile("(\\d+)");
    private static final Pattern HUSBOKSTAV_PATTERN = Pattern.compile("([A-ZÆØÅÁ])");

    public void execute(SkdMeldingTrans1 skdMeldingTrans1, PersonDTO person) {

        /* Boadresse */
        if (!person.getBostedsadresse().isEmpty()) {
            var boadresse = person.getBostedsadresse().getFirst();

            if (nonNull(boadresse.getMatrikkeladresse())) {
                var matrikkeladresse = boadresse.getMatrikkeladresse();
                skdMeldingTrans1.setAdressetype("M");
                skdMeldingTrans1.setGateGaard(prepad(matrikkeladresse.getGaardsnummer().toString(), 5));
                skdMeldingTrans1.setHusBruk(prepad(matrikkeladresse.getBruksnummer().toString(), 4));
                skdMeldingTrans1.setAdressenavn(matrikkeladresse.getTilleggsnavn());
                skdMeldingTrans1.setKommunenummer(matrikkeladresse.getKommunenummer());
                skdMeldingTrans1.setPostnummer(matrikkeladresse.getPostnummer());
                skdMeldingTrans1.setTilleggsadresse(matrikkeladresse.getTilleggsnavn());

            } else if (nonNull(boadresse.getVegadresse())) {
                var vegadresse = boadresse.getVegadresse();
                skdMeldingTrans1.setAdressetype("O");
                skdMeldingTrans1.setGateGaard(fixGatekode(vegadresse.getAdressekode()));
                addHusBrukAndBokstavFestenr(skdMeldingTrans1, vegadresse);
                skdMeldingTrans1.setAdressenavn(StringUtils.abbreviate(vegadresse.getAdressenavn(), 25));
                skdMeldingTrans1.setKommunenummer(vegadresse.getKommunenummer());
                skdMeldingTrans1.setPostnummer(vegadresse.getPostnummer());
                skdMeldingTrans1.setTilleggsadresse(vegadresse.getTilleggsnavn());
            }

            skdMeldingTrans1.setFlyttedatoAdr(ConvertDateToStringUtility.yyyyMMdd(
                    enforceValidTpsDate(NullcheckUtil.nullcheckSetDefaultValue(boadresse.getAngittFlyttedato(),
                            HentDatoFraIdentUtility.extract(person.getIdent())))));
        } else {
            skdMeldingTrans1.setFlyttedatoAdr(ConvertDateToStringUtility.yyyyMMdd(enforceValidTpsDate(
                    HentDatoFraIdentUtility.extract(person.getIdent()))));
        }
    }

    private static String fixGatekode(String gatekode) {

        return format("%05d", isNotBlank(gatekode) ? Integer.parseInt(gatekode) : 0);
    }

    private static String prepad(String value, int length) {

        var formatter = "%1$" + length + 's';
        return format(formatter, nonNull(value) ? value : '0').replace(' ', '0');
    }

    private void addHusBrukAndBokstavFestenr(SkdMeldingTrans1 skdMeldingTrans1, VegadresseDTO gateadresse) {

        if (isNotBlank(gateadresse.getHusnummer())) {

            var husbokstavMatcher = HUSBOKSTAV_PATTERN.matcher(gateadresse.getHusnummer());
            var husnummerMatcher = HUSNUMMER_PATTERN.matcher(gateadresse.getHusnummer());

            if (husbokstavMatcher.find()) {
                skdMeldingTrans1.setBokstavFestenr(HusbokstavEncoder.encode(husbokstavMatcher.group(1)));
            }
            if (husnummerMatcher.find()) {
                skdMeldingTrans1.setHusBruk(husnummerMatcher.group(1));
            }
        }
    }
}
