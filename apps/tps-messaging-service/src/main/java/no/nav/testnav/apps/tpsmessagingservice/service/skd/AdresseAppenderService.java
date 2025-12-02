package no.nav.testnav.apps.tpsmessagingservice.service.skd;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.SkdMeldingTrans1;
import no.nav.testnav.apps.tpsmessagingservice.utils.ConvertDateToStringUtility;
import no.nav.testnav.apps.tpsmessagingservice.utils.HentDatoFraIdentUtility;
import no.nav.testnav.apps.tpsmessagingservice.utils.HusbokstavEncoder;
import no.nav.testnav.apps.tpsmessagingservice.utils.LandkodeEncoder;
import no.nav.testnav.apps.tpsmessagingservice.utils.NullcheckUtil;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.GateadresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
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
        if (nonNull(person.getBoadresse())) {

            if (AdresseDTO.Adressetype.GATE == person.getBoadresse().getAdressetype()) {

                var gateadresse = (GateadresseDTO) person.getBoadresse();
                skdMeldingTrans1.setAdressetype("O");
                skdMeldingTrans1.setGateGaard(fixGatekode(gateadresse.getGatekode()));
                addHusBrukAndBokstavFestenr(skdMeldingTrans1, gateadresse);
                skdMeldingTrans1.setAdressenavn(StringUtils.abbreviate(gateadresse.getAdresse(), 25));
                skdMeldingTrans1.setKommunenummer(gateadresse.getKommunenr());
                skdMeldingTrans1.setPostnummer(gateadresse.getPostnr());
                skdMeldingTrans1.setTilleggsadresse(gateadresse.getTilleggsadresse());
                skdMeldingTrans1.setFlyttedatoAdr(ConvertDateToStringUtility.yyyyMMdd(
                        enforceValidTpsDate(NullcheckUtil.nullcheckSetDefaultValue(gateadresse.getFlyttedato(),
                                HentDatoFraIdentUtility.extract(person.getIdent())))));

            } else if (AdresseDTO.Adressetype.MATR == person.getBoadresse().getAdressetype()) {

                var matrikkeladresse = (MatrikkeladresseDTO) person.getBoadresse();
                skdMeldingTrans1.setAdressetype("M");
                skdMeldingTrans1.setGateGaard(prepad(matrikkeladresse.getGardsnr(), 5));
                skdMeldingTrans1.setHusBruk(prepad(matrikkeladresse.getBruksnr(), 4));
                skdMeldingTrans1.setAdressenavn(matrikkeladresse.getMellomnavn());
                skdMeldingTrans1.setKommunenummer(matrikkeladresse.getKommunenr());
                skdMeldingTrans1.setPostnummer(matrikkeladresse.getPostnr());
                skdMeldingTrans1.setTilleggsadresse(matrikkeladresse.getTilleggsadresse());
                skdMeldingTrans1.setFlyttedatoAdr(ConvertDateToStringUtility.yyyyMMdd(
                        enforceValidTpsDate(NullcheckUtil.nullcheckSetDefaultValue(matrikkeladresse.getFlyttedato(),
                                HentDatoFraIdentUtility.extract(person.getIdent())))));
            }

        } else {
            skdMeldingTrans1.setFlyttedatoAdr(ConvertDateToStringUtility.yyyyMMdd(enforceValidTpsDate(
                    HentDatoFraIdentUtility.extract(person.getIdent()))));
        }

        /* Postadresse */
        if (nonNull(person.getPostadresse())) {

            var postadresse = person.getPostadresse();
            skdMeldingTrans1.setPostadresse1(postadresse.getPostLinje1());
            skdMeldingTrans1.setPostadresse2(postadresse.getPostLinje2());
            skdMeldingTrans1.setPostadresse3(postadresse.getPostLinje3());
            skdMeldingTrans1.setPostadresseLand(LandkodeEncoder.encode(postadresse.getPostLand()));
        }
    }

    private static String fixGatekode(String gatekode) {

        return format("%05d", isNotBlank(gatekode) ? Integer.parseInt(gatekode) : 0);
    }

    private static String prepad(String value, int length) {

        var formatter = "%1$" + length + 's';
        return format(formatter, nonNull(value) ? value : '0').replace(' ', '0');
    }

    private void addHusBrukAndBokstavFestenr(SkdMeldingTrans1 skdMeldingTrans1, GateadresseDTO gateadresse) {

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
