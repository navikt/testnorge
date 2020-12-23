package no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv;

import java.util.HashMap;
import java.util.Map;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Permisjon;
import no.nav.registre.testnorge.libs.csvconverter.CsvConverter;
import no.nav.registre.testnorge.libs.csvconverter.CsvHeader;
import no.nav.registre.testnorge.libs.csvconverter.ObjectConverter;
import no.nav.registre.testnorge.libs.csvconverter.RowConverter;

public class PermisjonSyntetiseringCsvConverter extends CsvConverter<Permisjon> {

    private static PermisjonSyntetiseringCsvConverter inst;

    private PermisjonSyntetiseringCsvConverter() {
    }

    public static PermisjonSyntetiseringCsvConverter inst() {
        if (inst == null) {
            inst = new PermisjonSyntetiseringCsvConverter();
        }
        return inst;
    }

    private enum Headers implements CsvHeader {
        KALENDERMAANED("KALENDERMAANED"),
        IDENT("IDENT"),
        STARTDATO("STARTDATO"),
        SLUTTDATO("SLUTTDATO"),
        PERMISJONSPROSENT("PERMISJONSPROSENT"),
        BESKRIVELSE("BESKRIVELSE");

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
    protected RowConverter<Permisjon> getRowConverter() {
        throw new UnsupportedOperationException("Import av arbeidsforhold ikke stottet");
    }

    @Override
    protected ObjectConverter<Permisjon> getObjectConverter() {
        return permisjon -> {
            Map<String, Object> map = new HashMap<>();
            map.put(Headers.KALENDERMAANED.getValue(), permisjon.getKalendermaaned());
            map.put(Headers.IDENT.getValue(), permisjon.getIdent());
            map.put(Headers.STARTDATO.getValue(), permisjon.getStartdato());
            map.put(Headers.SLUTTDATO.getValue(), permisjon.getSluttdato());
            map.put(Headers.PERMISJONSPROSENT.getValue(), permisjon.getPermisjonsprosent());
            map.put(Headers.BESKRIVELSE.getValue(), permisjon.getBeskrivelse());
            return map;
        };
    }

    @Override
    protected CsvHeader[] getHeaders() {
        return Headers.values();
    }
}
