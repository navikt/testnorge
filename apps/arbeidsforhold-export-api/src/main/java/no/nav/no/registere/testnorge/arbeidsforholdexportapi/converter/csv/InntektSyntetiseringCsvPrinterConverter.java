package no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Inntekt;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Permisjon;
import no.nav.registre.testnorge.libs.csvconverter.CsvHeader;
import no.nav.registre.testnorge.libs.csvconverter.CsvPrinterConverter;
import no.nav.registre.testnorge.libs.csvconverter.ObjectConverter;

public class InntektSyntetiseringCsvPrinterConverter extends CsvPrinterConverter<Inntekt> {

    public InntektSyntetiseringCsvPrinterConverter(PrintWriter writer) {
        super(writer);
    }

    private enum Headers implements CsvHeader {
        RAPPORTERINGSMAANED("RAPPORTERINGSMAANED"),
        ARBEIDSFORHOLD_TYPE("ARBEIDSFORHOLD_TYPE"),
        STARTDATO_OPPTJENINGSPERIODE("STARTDATO_OPPTJENINGSPERIODE"),
        SLUTTDATO_OPPTJENINGSPERIODE("SLUTTDATO_OPPTJENINGSPERIODE"),
        ANTALL("ANTALL"),
        OPPTJENINGSLAND("OPPTJENINGSLAND");

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
    protected ObjectConverter<Inntekt> getObjectConverter() {
        return inntekt -> {
            Map<String, Object> map = new HashMap<>();
            map.put(Headers.RAPPORTERINGSMAANED.getValue(), inntekt.getKalendermaaned());
            map.put(Headers.ARBEIDSFORHOLD_TYPE.getValue(), inntekt.getTypeArbeidsforhold());
            map.put(Headers.STARTDATO_OPPTJENINGSPERIODE.getValue(), inntekt.getStartdatoOpptjeningsperiode());
            map.put(Headers.SLUTTDATO_OPPTJENINGSPERIODE.getValue(), inntekt.getSluttdatoOpptjeningsperiode());
            map.put(Headers.ANTALL.getValue(), inntekt.getAntall());
            map.put(Headers.OPPTJENINGSLAND.getValue(), inntekt.getOpptjeningsland());
            return map;
        };
    }

    @Override
    protected CsvHeader[] getHeaders() {
        return Headers.values();
    }
}
