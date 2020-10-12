package no.nav.registre.testnorge.personexportapi.converter.csv;

import java.util.HashMap;
import java.util.Map;

import no.nav.registre.testnorge.libs.csvconverter.CsvConverter;
import no.nav.registre.testnorge.libs.csvconverter.CsvHeader;
import no.nav.registre.testnorge.libs.csvconverter.ObjectConverter;
import no.nav.registre.testnorge.libs.csvconverter.RowConverter;
import no.nav.registre.testnorge.personexportapi.domain.Person;

public class NorskHelsenettPersonCsvConverter extends CsvConverter<Person> {

    private static NorskHelsenettPersonCsvConverter inst;

    private NorskHelsenettPersonCsvConverter() {
    }

    public static NorskHelsenettPersonCsvConverter inst() {
        if (inst == null) {
            inst = new NorskHelsenettPersonCsvConverter();
        }
        return inst;
    }

    private enum Headers implements CsvHeader {
        FNR("FNR"),
        KORTNAVN("KORTNAVN"),
        FORNAVN("FORNAVN"),
        MELLOMNAVN("MELLOMNAVN"),
        ETTERNAVN("ETTERNAVN"),
        PIKENAVN("PIKENAVN"),
        KJONN("KJONN"),
        BESKR_KJONN("BESKR_KJONN"),
        FODT_DATO("FODT_DATO"),
        DOD_DATO("DOD_DATO"),
        PERSONSTATUS("PERSONSTATUS"),
        BESKR_PERSONSTATUS("BESKR_PERSONSTATUS"),
        PERSONSTATUS_DATO("PERSONSTATUS_DATO"),
        SIVILSTAND("SIVILSTAND"),
        BESKR_SIVILSTAND("BESKR_SIVILSTAND"),
        SIVILSTAND_DATO("SIVILSTAND_DATO"),
        STATSBORGERLAND("STATSBORGERLAND"),
        STATSBORGER_DATO("STATSBORGER_DATO"),
        GATENAVN("GATENAVN"),
        CO_ADRESSE("CO_ADRESSE"),
        HUSNR("HUSNR"),
        HUSBOKSTAV("HUSBOKSTAV"),
        BOLIGNR("BOLIGNR"),
        GATENR("GATENR"),
        GARDSNR("GARDSNR"),
        BRUKSNR("BRUKSNR"),
        STEDSNAVN("STEDSNAVN"),
        KOMMUNENR("KOMMUNENR"),
        KOMMUNE("KOMMUNE"),
        DATO_ADR_FRA("DATO_ADR_FRA"),
        ADRESSEKODE("ADRESSEKODE"),
        REG_DATO_ADR_KD("REG_DATO_ADR_KD"),
        POSTADR_1("POSTADR_1"),
        POSTADR_2("POSTADR_2"),
        POSTADR_3("POSTADR_3"),
        POSTNR("POSTNR"),
        POSTSTED("POSTSTED"),
        POSTADR_REG_DATO("POSTADR_REG_DATO"),
        POSTADR_LAND("POSTADR_LAND"),
        BYDEL("BYDEL"),
        BYDELS_NAVN("BYDELS_NAVN"),
        INNVANDRET_FRA_LAND("INNVANDRET_FRA_LAND"),
        INNVANDRET_DATO("INNVANDRET_DATO"),
        UTVANDRET_TIL_LAND("UTVANDRET_TIL_LAND"),
        UTVANDRET_DATO("UTVANDRET_DATO"),
        SKOLEKRETS("SKOLEKRETS"),
        VALGKRETS("VALGKRETS"),
        GRUNNKRETS("GRUNNKRETS");

        private final String header;

        Headers(String header) {
            this.header = header;
        }

        @Override
        public String getValue() {
            return header;
        }
    }

    @Override
    protected RowConverter<Person> getRowConverter() {
        throw new UnsupportedOperationException("Import av person ikke stottet");
    }

    @Override
    protected ObjectConverter<Person> getObjectConverter() {
        return person -> {
            Map<String, Object> map = new HashMap<>();
            map.put(Headers.FNR.getValue(), person.getIdent());
            map.put(Headers.FORNAVN.getValue(), person.getFornavn());
            map.put(Headers.MELLOMNAVN.getValue(), person.getMellomnavn());
            map.put(Headers.ETTERNAVN.getValue(), person.getEtternavn());
            return map;
        };
    }

    @Override
    protected CsvHeader[] getHeaders() {
        return Headers.values();
    }
}
