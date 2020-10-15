package no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv;

import java.util.HashMap;
import java.util.Map;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Arbeidsforhold;
import no.nav.registre.testnorge.libs.csvconverter.CsvConverter;
import no.nav.registre.testnorge.libs.csvconverter.CsvHeader;
import no.nav.registre.testnorge.libs.csvconverter.ObjectConverter;
import no.nav.registre.testnorge.libs.csvconverter.RowConverter;

public class ArbeidsforholdSyntentiseringCsvConverter extends CsvConverter<Arbeidsforhold> {

    private static ArbeidsforholdSyntentiseringCsvConverter inst;

    private ArbeidsforholdSyntentiseringCsvConverter() {
    }

    public static ArbeidsforholdSyntentiseringCsvConverter inst() {
        if (inst == null) {
            inst = new ArbeidsforholdSyntentiseringCsvConverter();
        }
        return inst;
    }

    private enum Headers implements CsvHeader {
        RAPPORTERINGSMAANED("RAPPORTERINGSMÅNED"),
        OPPLYSNINGSPLIKTIG("OPPLYSNINGSPLIKTIG"),
        VIRKSOMHET("VIRKSOMHET"),
        ARBEIDSFORHOLD_ID("ARBEIDSFORHOLD_ID"),
        ARBEIDSFORHOLD_TYPE("ARBEIDSFORHOLD_TYPE"),
        YRKESKODE("YRKESKODE"),
        STILLINGSPROSENT("STILLINGSPROSENT"),
        STARTDATO("STARTDATO"),
        SLUTTDATO("SLUTTDATO"),
        IDENT("IDENT");

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
    protected RowConverter<Arbeidsforhold> getRowConverter() {
        throw new UnsupportedOperationException("Import av arbeidsforhold ikke stottet");
    }

    @Override
    protected ObjectConverter<Arbeidsforhold> getObjectConverter() {
        return arbeidsforhold -> {
            Map<String, Object> map = new HashMap<>();
            map.put(Headers.RAPPORTERINGSMAANED.getValue(), arbeidsforhold.getKalendermaaned());
            map.put(Headers.OPPLYSNINGSPLIKTIG.getValue(), arbeidsforhold.getOpplysningspliktigOrgnummer());
            map.put(Headers.VIRKSOMHET.getValue(), arbeidsforhold.getVirksomhetOrgnummer());
            map.put(Headers.ARBEIDSFORHOLD_ID.getValue(), arbeidsforhold.getArbeidsforholdId());
            map.put(Headers.ARBEIDSFORHOLD_TYPE.getValue(), arbeidsforhold.getArbeidsforholdType());
            map.put(Headers.YRKESKODE.getValue(), arbeidsforhold.getYrkekode());
            map.put(Headers.STILLINGSPROSENT.getValue(), arbeidsforhold.getStillingsprosent());
            map.put(Headers.STARTDATO.getValue(), arbeidsforhold.getStatdato());
            map.put(Headers.SLUTTDATO.getValue(), arbeidsforhold.getSluttdato());
            map.put(Headers.IDENT.getValue(), arbeidsforhold.getIdent());
            return map;
        };
    }

    @Override
    protected CsvHeader[] getHeaders() {
        return Headers.values();
    }
}
