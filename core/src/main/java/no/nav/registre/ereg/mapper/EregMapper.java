package no.nav.registre.ereg.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import no.nav.registre.ereg.provider.rs.request.Adresse;
import no.nav.registre.ereg.provider.rs.request.EregDataRequest;
import no.nav.registre.ereg.provider.rs.request.Maalform;
import no.nav.registre.ereg.provider.rs.request.Telefon;
import no.nav.registre.ereg.provider.rs.request.UtenlandsRegister;

@Slf4j
@AllArgsConstructor
@Getter
@Setter
@Component
public class EregMapper {

    public String mapEregFromRequests(List<EregDataRequest> data) {

        StringBuilder eregFile = new StringBuilder(makeHeader());
        int units = 0;
        int totalRecords = 0;
        for (EregDataRequest eregDataRequest : data) {

            RecordsAndCount unit = createUnit(eregDataRequest);
            totalRecords += unit.numRecords;
            eregFile.append(unit.val);
            units++;
        }

        eregFile.append(createTrailer(units, totalRecords));

        return eregFile.toString();
    }

    private RecordsAndCount createUnit(EregDataRequest data) {
        int numRecords = 2;

        String file = createENH(data.getOrgId(), data.getType()) + createNavn(data.getNavn());

        Adresse adresse = data.getAdresse();
        if (adresse != null) {
            file += createAdresse("PADR", adresse.getAdresser(), adresse.getPostNr(), adresse.getLandKode(), adresse.getKommuneNr(), adresse.getPostSted());
            numRecords++;
        }

        Adresse forretningsAdresse = data.getForretningsAdresse();
        if (forretningsAdresse != null) {
            file += createAdresse("FADR", forretningsAdresse.getAdresser(), forretningsAdresse.getPostNr(), forretningsAdresse.getLandKode(), forretningsAdresse.getKommuneNr(), forretningsAdresse.getPostSted());
            numRecords++;
        }

        String epost = data.getEpost();
        if (epost != null) {
            file += createIAdresse("EPOS", epost);
            numRecords++;
        }

        String iAdr = data.getInternetAdresse();
        if (iAdr != null) {
            file += createIAdresse("IADR", iAdr);
            numRecords++;
        }

        Maalform maalform = data.getMaalform();
        if (maalform != null) {
            file += createMaalform(maalform);
            numRecords++;
        }

        Boolean harAnsatte = data.getHarAnsatte();
        if (harAnsatte != null) {
            if (harAnsatte) {
                file += createArbeidsgiverHarAnsatte("J");
            } else {
                file += createArbeidsgiverHarAnsatte("N");
            }
            numRecords++;
        }

        String sektorKode = data.getSektorKode();
        if (sektorKode != null) {
            file += createSektorKode(sektorKode);
            numRecords++;
        }

        String stiftelsesDato = data.getStiftelsesDato();
        if (stiftelsesDato != null) {
            file += createStiftelsesDato(stiftelsesDato);
            numRecords++;
        }

        Telefon telefon = data.getTelefon();
        if (telefon != null) {
            if (telefon.getFast() != null) {
                file += createTlf("TFON", telefon.getFast());
                numRecords++;
            }
            if (telefon.getFax() != null) {
                file += createTlf("TFAX", telefon.getFax());
                numRecords++;
            }
            if (telefon.getMobil() != null) {
                file += createTlf("MTLF", telefon.getMobil());
                numRecords++;
            }
        }

        String frivillighetsKode = data.getFrivillighetsKode();
        if (frivillighetsKode != null) {
            file += createFrivilligKategori(frivillighetsKode, "1");
            numRecords++;
        }

        String nedleggelse = data.getNedleggelsesDato();
        if (nedleggelse != null) {
            file += createDatoRecord("NDATN", nedleggelse);
            numRecords++;
        }
        String oppstart = data.getOppstartsDato();
        if (oppstart != null) {
            file += createDatoRecord("BDATN", oppstart);
            numRecords++;
        }
        String eierskapskifte = data.getEierskapskifteDato();
        if (eierskapskifte != null) {
            file += createDatoRecord("EDATN", eierskapskifte);
            numRecords++;
        }

        Boolean kjoensfordeling = data.getKjoensfordeling();
        if (kjoensfordeling != null) {
            file += createVirksomhetInfo("KJRPN", kjoensfordeling);
            numRecords++;
        }

        Boolean utelukkendeVirksomhetINorge = data.getUtelukkendeVirksomhetINorge();
        if (utelukkendeVirksomhetINorge != null) {
            file += createVirksomhetInfo("UVNON", utelukkendeVirksomhetINorge);
            numRecords++;
        }

        Boolean heleidINorge = data.getUtelukkendeVirksomhetINorge();
        if (heleidINorge != null) {
            file += createVirksomhetInfo("UENON", heleidINorge);
            numRecords++;
        }

        Boolean fravalgAvRevisjonen = data.getUtelukkendeVirksomhetINorge();
        if (fravalgAvRevisjonen != null) {
            file += createVirksomhetInfo("RVFGN", fravalgAvRevisjonen);
            numRecords++;
        }

        UtenlandsRegister utenlandsRegister = data.getUtenlandsRegister();
        if (utenlandsRegister != null) {
            file += createHjemlandsRegister(utenlandsRegister.getNavn(), utenlandsRegister.getRegisterNr(), utenlandsRegister.getAdresse());
            numRecords++;
        }

        Map<String, String> statuser = data.getStatuser();
        if (statuser != null) {
            StringBuilder stringbuilder = new StringBuilder(8 * statuser.size());
            for (Map.Entry<String, String> entry : statuser.entrySet()) {
                stringbuilder.append(createStatus(entry.getKey(), entry.getValue()));
                numRecords++;
            }
            file += stringbuilder.toString();
        }

        return new RecordsAndCount(file, numRecords);
    }

    private String makeHeader() {
        return "HEADER " + getDateNowFormatted() + "0000" + "AA A\n";
    }

    private String createENH(String orgId, String unitType) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(49, ' ');

        String dateNowFormatted = getDateNowFormatted();

        stringBuilder.replace(0, "ENH".length(), "ENH")
                .replace(4, 4 + orgId.length(), orgId)
                .replace(13, 13 + unitType.length(), unitType)
                .replace(17, 17 + "NNY".length(), "NNY")
                .replace(22, 22 + (dateNowFormatted + dateNowFormatted).length(), dateNowFormatted + dateNowFormatted + "J")
                .append("\n");
        return stringBuilder.toString();
    }

    private String createNavn(String name) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(219, ' ');
        stringBuilder.replace(0, "NAVNN".length(), "NAVNN")
                .replace(8, 8 + name.length(), name)
                .append("\n");
        return stringBuilder.toString();
    }

    private String createAdresse(String type, List<String> addresses, String postNr, String landCode, String kommuneNr, String postSted) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(185, ' ');
        stringBuilder.replace(0, type.length() + 1, type + "N")
                .replace(8, 8 + postNr.length(), postNr)
                .replace(17, 17 + landCode.length(), landCode)
                .replace(20, 20 + kommuneNr.length(), kommuneNr)
                .replace(30, 30 + postSted.length(), postSted);
        if (addresses.size() > 3) {
            log.warn("Antall addresser er for mange, bruker de 3 første");
        }

        concatListToString(stringBuilder, addresses, 64);

        return stringBuilder.toString();
    }

    private String createIAdresse(String type, String adr) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(158, ' ');
        stringBuilder.replace(0, type.length() + 1, type + "N");
        stringBuilder.replace(8, 8 + adr.length(), adr);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    private String createMaalform(Maalform type) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(8, ' ');
        stringBuilder.replace(0, "MÅL N".length(), "MÅL N").replace(8, 9, type.getForm()).append("\n");
        return stringBuilder.toString();
    }

    private String createArbeidsgiverHarAnsatte(String harAnsatte) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(8, ' ');
        stringBuilder.replace(0, "ARBGN".length(), "ARBGN").replace(8, 9, harAnsatte).append("\n");
        return stringBuilder.toString();
    }

    private String createSektorKode(String sektorKode) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(12, ' ');
        stringBuilder.replace(0, "ISEKN".length(), "ISEKN")
                .replace(8, 8 + sektorKode.length(), sektorKode).append("\n");
        return stringBuilder.toString();
    }

    private String createStiftelsesDato(String dato) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(16, ' ');
        stringBuilder.replace(0, "STIDN".length(), "STIDN")
                .replace(8, 8 + dato.length(), dato).append("\n");
        return stringBuilder.toString();
    }

    private String createTlf(String type, String tlf) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(16, ' ');
        stringBuilder.replace(0, type.length() + 1, type + "N")
                .replace(8, 8 + tlf.length(), tlf).append("\n");
        return stringBuilder.toString();
    }

    private String createFrivilligKategori(String kode, String rangering) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(14, ' ');
        stringBuilder.replace(0, "KATGN".length(), "KATGN")
                .replace(8, 8 + kode.length(), kode)
                .replace(13, 14, rangering)
                .append("\n");
        return stringBuilder.toString();
    }

    private String createVirksomhetInfo(String type, Boolean boolskVerdi) {
        String verdi = boolskVerdi ? "J" : "N";
        StringBuilder stringBuilder = createStringBuilderWithReplacement(9, ' ');
        stringBuilder.replace(0, type.length(), type)
                .replace(8, 8 + verdi.length(), verdi).append("\n");
        return stringBuilder.toString();
    }

    private String createDatoRecord(String type, String dato) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(16, ' ');
        stringBuilder.replace(0, type.length(), type)
                .replace(8, 8 + dato.length(), dato).append("\n");
        return stringBuilder.toString();
    }

    private String createHjemlandsRegister(List<String> navn, String registerNr, Adresse adresse) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(291, ' ');

        stringBuilder.replace(0, "UREGN".length(), "UREGN")
                .replace(8, 8 + registerNr.length(), registerNr);

        concatListToString(stringBuilder, navn, 43);

        stringBuilder.replace(148, 148 + adresse.getLandKode().length(), adresse.getLandKode())
                .replace(152, 152 + adresse.getPostSted().length(), adresse.getPostSted());

        List<String> addresses = adresse.getAdresser();

        concatListToString(stringBuilder, addresses, 186);

        return stringBuilder.toString();
    }

    private void concatListToString(StringBuilder stringBuilder, List<String> addresses, int indexStart) {
        int iterSize = 3;
        if (addresses.size() < iterSize) {
            iterSize = addresses.size();
        }

        int start = indexStart;
        for (int i = 0; i < iterSize; i++) {
            stringBuilder.replace(start, start + addresses.get(i).length(), addresses.get(i));
            start = start + 35 * (i + 1);
        }

        stringBuilder.append("\n");
    }

    private String createStatus(String statusType, String endringsType) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(8, ' ');
        stringBuilder.replace(0, statusType.length(), statusType)
                .replace(4, 4 + endringsType.length(), endringsType).append("\n");
        return stringBuilder.toString();
    }

    private String createTrailer(int units, int records) {
        //Legger til header og trailer i antall records
        records = records + 2;
        StringBuilder stringBuilder = createStringBuilderWithReplacement(23, '0');
        stringBuilder.replace(0, 6, "TRAIER ")
                .replace(15 - String.valueOf(units).length(), 15, String.valueOf(units))
                .replace(24 - String.valueOf(records).length(), 24, String.valueOf(records))
                .append("\n");
        return stringBuilder.toString();
    }

    private StringBuilder createStringBuilderWithReplacement(int size, char replacement) {
        StringBuilder stringBuilder = new StringBuilder(size);
        stringBuilder.setLength(size);
        for (int i = 0; i < stringBuilder.length(); i++) {
            stringBuilder.setCharAt(i, replacement);
        }
        return stringBuilder;
    }

    private String getDateNowFormatted() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date());
    }

    @AllArgsConstructor
    private class RecordsAndCount {

        public String val;
        public int numRecords;
    }

}
