package no.nav.registre.sdforvalter.converter.csv;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import no.nav.registre.sdforvalter.domain.Adresse;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.testnorge.libs.csvconverter.v1.CsvConverter;
import no.nav.registre.testnorge.libs.csvconverter.v1.CsvHeader;
import no.nav.registre.testnorge.libs.csvconverter.v1.ObjectConverter;
import no.nav.registre.testnorge.libs.csvconverter.v1.RowConverter;

public class EregCsvConverter extends CsvConverter<Ereg> {
    private static final String LIST_SPLITT_CHARACTER = ",";

    static EregCsvConverter inst;

    private EregCsvConverter() {
    }

    public static EregCsvConverter inst() {
        if (inst == null) {
            inst = new EregCsvConverter();
        }
        return inst;
    }

    private enum Headers implements CsvHeader {
        ORGNUMMER("Organisasjonsnummer*"),
        ENHETSTYPE("Enhetstype*"),
        NAVN("Navn"),
        REDIGERT_NAVN("Redigert navn"),
        EPOST("Epost"),
        JURIDISK_ENHET("Juridisk enhet"),
        GRUPPE("Gruppe"),
        OPPRINNELSE("Opprinnelse"),
        FORRETNINGS_ADRESSE("Forretnings adresse"),
        FORRETNINGS_POSTNR("Forretnings postnr"),
        FORRETNINGS_KOMMUNENR("Forretnings kommunenr"),
        FORRETNINGS_LANDKODE("Forretnings landkode"),
        FORRETNINGS_POSTSTED("Forretnings poststed"),
        POSTADRESSE_ADRESSE("Postadresse adresse"),
        POSTADRESSE_POSTNR("Postadresse postnr"),
        POSTADRESSE_KOMMUNENR("Postadresse kommunenr"),
        POSTADRESSE_LANDKODE("Postadresse landkode"),
        POSTADRESSE_POSTSTED("Postadresse poststed"),
        TAGS("Tags");

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
    protected RowConverter<Ereg> getRowConverter() {
        return map -> Ereg
                .builder()
                .orgnr(getString(map, Headers.ORGNUMMER))
                .enhetstype(getString(map, Headers.ENHETSTYPE))
                .navn(getString(map, Headers.NAVN))
                .redigertNavn(getString(map, Headers.REDIGERT_NAVN))
                .epost(getString(map, Headers.EPOST))
                .juridiskEnhet(getString(map, Headers.JURIDISK_ENHET))
                .gruppe(getString(map, Headers.GRUPPE))
                .opprinnelse(getString(map, Headers.OPPRINNELSE))
                .forretningsAdresse(Adresse
                        .builder()
                        .adresse(getString(map, Headers.FORRETNINGS_ADRESSE))
                        .postnr(getString(map, Headers.FORRETNINGS_POSTNR))
                        .kommunenr(getString(map, Headers.FORRETNINGS_KOMMUNENR))
                        .landkode(getString(map, Headers.FORRETNINGS_LANDKODE))
                        .poststed(getString(map, Headers.FORRETNINGS_POSTSTED))
                        .build()
                ).postadresse(Adresse
                        .builder()
                        .adresse(getString(map, Headers.POSTADRESSE_ADRESSE))
                        .postnr(getString(map, Headers.POSTADRESSE_POSTNR))
                        .kommunenr(getString(map, Headers.POSTADRESSE_KOMMUNENR))
                        .landkode(getString(map, Headers.POSTADRESSE_LANDKODE))
                        .poststed(getString(map, Headers.POSTADRESSE_POSTSTED))
                        .build()
                )
                .tags(getString(map, Headers.TAGS) == null
                        ? Collections.emptySet()
                        : Set.of(getString(map, Headers.TAGS).split(LIST_SPLITT_CHARACTER))
                ).build();
    }

    @Override
    protected ObjectConverter<Ereg> getObjectConverter() {
        return item -> {
            Map<String, Object> map = new HashMap<>();
            map.put(Headers.ORGNUMMER.getValue(), item.getOrgnr());
            map.put(Headers.ENHETSTYPE.getValue(), item.getEnhetstype());
            map.put(Headers.NAVN.getValue(), item.getNavn());
            map.put(Headers.REDIGERT_NAVN.getValue(), item.getRedigertNavn());
            map.put(Headers.EPOST.getValue(), item.getEpost());
            map.put(Headers.GRUPPE.getValue(), item.getGruppe());
            map.put(Headers.JURIDISK_ENHET.getValue(), item.getJuridiskEnhet());
            map.put(Headers.OPPRINNELSE.getValue(), item.getOpprinnelse());
            Adresse forretningsAdresse = item.getForretningsAdresse();
            if (forretningsAdresse != null) {
                map.put(Headers.FORRETNINGS_ADRESSE.getValue(), forretningsAdresse.getAdresse());
                map.put(Headers.FORRETNINGS_POSTNR.getValue(), forretningsAdresse.getPostnr());
                map.put(Headers.FORRETNINGS_KOMMUNENR.getValue(), forretningsAdresse.getKommunenr());
                map.put(Headers.FORRETNINGS_LANDKODE.getValue(), forretningsAdresse.getLandkode());
                map.put(Headers.FORRETNINGS_POSTSTED.getValue(), forretningsAdresse.getPoststed());
            }
            Adresse postadresse = item.getPostadresse();
            if (postadresse != null) {
                map.put(Headers.POSTADRESSE_ADRESSE.getValue(), postadresse.getAdresse());
                map.put(Headers.POSTADRESSE_POSTNR.getValue(), postadresse.getPostnr());
                map.put(Headers.POSTADRESSE_KOMMUNENR.getValue(), postadresse.getKommunenr());
                map.put(Headers.POSTADRESSE_LANDKODE.getValue(), postadresse.getLandkode());
                map.put(Headers.POSTADRESSE_POSTSTED.getValue(), postadresse.getPoststed());
            }
            map.put(Headers.TAGS.getValue(), String.join(LIST_SPLITT_CHARACTER, item.getTags()));
            return map;
        };
    }

    @Override
    protected CsvHeader[] getHeaders() {
        return Headers.values();
    }
}
