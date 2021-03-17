package no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Avvik;
import no.nav.registre.testnorge.libs.csvconverter.CsvHeader;
import no.nav.registre.testnorge.libs.csvconverter.CsvPrinterConverter;
import no.nav.registre.testnorge.libs.csvconverter.ObjectConverter;

public class AvvikSyntetiseringCsvPrinterConverter extends CsvPrinterConverter<Avvik> {

    public AvvikSyntetiseringCsvPrinterConverter(PrintWriter writer) {
        super(writer);
    }

    private enum Headers implements CsvHeader {
        TYPE("TYPE"),
        ARBEIDSFORHOLD_TYPE("ARBEIDSFORHOLD_TYPE"),
        ID("ID"),
        NAVN("NAVN"),
        DETALJER("DETALJER"),
        ALVORLIGHETSGRAD("ALVORLIGHETSGRAD");

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
    protected ObjectConverter<Avvik> getObjectConverter() {
        return avvik -> {
            Map<String, Object> map = new HashMap<>();
            map.put(Headers.TYPE.getValue(), avvik.getType());
            map.put(Headers.ARBEIDSFORHOLD_TYPE.getValue(), avvik.getArbeidsforholdType());
            map.put(Headers.ID.getValue(), avvik.getId());
            map.put(Headers.NAVN.getValue(), avvik.getNavn());
            map.put(Headers.DETALJER.getValue(), avvik.getDetaljer());
            map.put(Headers.ALVORLIGHETSGRAD.getValue(), avvik.getAlvorlighetsgrad());
            return map;
        };
    }

    @Override
    protected CsvHeader[] getHeaders() {
        return Headers.values();
    }
}
