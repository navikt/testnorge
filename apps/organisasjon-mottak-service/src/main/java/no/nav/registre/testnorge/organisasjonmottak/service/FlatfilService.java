package no.nav.registre.testnorge.organisasjonmottak.service;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

import no.nav.registre.testnorge.organisasjonmottak.domain.Base;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;
import no.nav.registre.testnorge.organisasjonmottak.domain.Knyttning;
import no.nav.registre.testnorge.organisasjonmottak.domain.Navn;
import no.nav.registre.testnorge.organisasjonmottak.domain.Organiasjon;
import no.nav.registre.testnorge.organisasjonmottak.domain.Record;

@Service
public class FlatfilService {

    private static String getDateNowFormatted() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date());
    }

    public Flatfil toFlatfil(Organiasjon organiasjon) {
        Flatfil flatfil = new Flatfil();
        Record record = new Record();
        record.append(createEHN(organiasjon, false));
        record.append(createNavn(organiasjon.getNavn()));
        flatfil.add(record);
        return flatfil;
    }

    public Flatfil toFlatfil(Navn navn) {
        Flatfil flatfil = new Flatfil();
        Record record = new Record();
        record.append(createEHN(navn, true));
        record.append(createNavn(navn.getNavn()));
        flatfil.add(record);
        return flatfil;
    }


    private String createEHN(Base base, boolean update) {
        return createEHN(base.getOrgnummer(), base.getEnhetstype(), update);
    }

    private String createEHN(Knyttning knyttning, boolean update) {
        return createEHN(knyttning.getOverenhetOrgnummer(), knyttning.getOverenhetEnhetstype(), update);
    }


    private String createEHN(String orgnummer, String enhetstype, boolean update) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(49, ' ');

        String dateNowFormatted = getDateNowFormatted();
        String undersakstype = update ? "EN" : "NY";

        stringBuilder.replace(0, "ENH".length(), "ENH")
                .replace(4, 4 + orgnummer.length(), orgnummer)
                .replace(13, 13 + enhetstype.length(), enhetstype)
                .replace(17, 18, update ? "E" : "N")
                .replace(18, 18 + undersakstype.length(), undersakstype)
                .replace(22, 22 + (dateNowFormatted + dateNowFormatted).length(), dateNowFormatted + dateNowFormatted + "J")
                .append("\n");

        return stringBuilder.toString();
    }


    private String createNavn(String navn) {
        StringBuilder stringBuilder = createBaseStringbuilder(219, "NAVN", "N");
        stringBuilder.replace(8, 8 + navn.length(), navn).append("\n");
        stringBuilder.replace(183, 183 + navn.length(), navn).append("\n");
        return stringBuilder.toString();
    }

    private StringBuilder createBaseStringbuilder(int size, String type, String endringsType) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(size, ' ');
        stringBuilder.replace(0, type.length(), type);
        stringBuilder.replace(4, 5, endringsType);
        return stringBuilder;
    }

    private StringBuilder createStringBuilderWithReplacement(int size, char replacement) {
        StringBuilder stringBuilder = new StringBuilder(size);
        stringBuilder.setLength(size);
        for (int i = 0; i < stringBuilder.length(); i++) {
            stringBuilder.setCharAt(i, replacement);
        }
        return stringBuilder;
    }

    private String createKnyttning(Knyttning knyttning) {
        return createKnyttning(knyttning.getUnderenhetOrgnummer(), knyttning.getUnderenhetEnhetstype());
    }


    private String createKnyttning(
            String type,
            String orgNr) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(66, ' ');
        StringBuilder typeBuilder = new StringBuilder("    NSSY").replace(0, type.length(), type);
        stringBuilder
                .replace(0, 8, typeBuilder.toString())
                .replace(8, 9, "K")
                .replace(9, 10, "D")
                .replace(41, 41 + getStringLength(orgNr), Strings.nullToEmpty(orgNr))
                .append("\n");
        return stringBuilder.toString();
    }

    private int getStringLength(String value) {
        return value == null ? 0 : value.length();
    }


    public Flatfil toFlatfil(Knyttning knyttning) {
        Flatfil flatfil = new Flatfil();
        Record record = new Record();
        record.append(createEHN(knyttning, false));
        record.append(createKnyttning(knyttning));
        flatfil.add(record);
        return flatfil;
    }
}
