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
        RAPPORTERINGSMAANED("RAPPORTERINGSMAANED"),
        IDENT("IDENT"),
        OPPLYSNINGSPLIKTIG("OPPLYSNINGSPLIKTIG"),
        VIRKSOMHET("VIRKSOMHET"),
        ARBEIDSFORHOLD_ID("ARBEIDSFORHOLD_ID"),
        ARBEIDSFORHOLD_TYPE("ARBEIDSFORHOLD_TYPE"),
        STARTDATO("STARTDATO"),
        SLUTTDATO("SLUTTDATO"),
        ANTALL_TIMER_PER_UKE_SOM_EN_FULL_STILLING_TILSVARER("ANTALL_TIMER_PER_UKE_SOM_EN_FULL_STILLING_TILSVARER"),
        AVLOENNINGSTYPE("AVLOENNINGSTYPE"),
        YRKE("YRKE"),
        ARBEIDSTIDSORDNING("ARBEIDSTIDSORDNING"),
        STILLINGSPROSENT("STILLINGSPROSENT"),
        SISTE_LOENNSENDRINGSDATO("SISTE_LOENNSENDRINGSDATO"),
        SISTE_DATO_FOR_STILLINGSPROSENTENDRING("SISTE_DATO_FOR_STILLINGSPROSENTENDRING"),
        PERMISJON_MED_FORELDREPENGER("PERMISJON_MED_FORELDREPENGER"),
        PERMITTERING("PERMITTERING"),
        PERMISJON("PERMISJON"),
        PERMISJON_VED_MILITAERTJENESTE("PERMISJON_VED_MILITAERTJENESTE"),
        VELFERDSPERMISJON("VELFERDSPERMISJON"),
        UTDANNINGSPERMISJON("UTDANNINGSPERMISJON");

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
            map.put(Headers.IDENT.getValue(), arbeidsforhold.getIdent());
            map.put(Headers.OPPLYSNINGSPLIKTIG.getValue(), arbeidsforhold.getOpplysningspliktigOrgnummer());
            map.put(Headers.VIRKSOMHET.getValue(), arbeidsforhold.getVirksomhetOrgnummer());
            map.put(Headers.ARBEIDSFORHOLD_ID.getValue(), arbeidsforhold.getArbeidsforholdId());
            map.put(Headers.ARBEIDSFORHOLD_TYPE.getValue(), arbeidsforhold.getArbeidsforholdType());
            map.put(Headers.STARTDATO.getValue(), arbeidsforhold.getStartdato());
            map.put(Headers.SLUTTDATO.getValue(), arbeidsforhold.getSluttdato());
            map.put(Headers.ANTALL_TIMER_PER_UKE_SOM_EN_FULL_STILLING_TILSVARER.getValue(), arbeidsforhold.getAntallTimerPerUkeSomEnFullStillingTilsvarer());
            map.put(Headers.AVLOENNINGSTYPE.getValue(), arbeidsforhold.getAvloenningstype());
            map.put(Headers.YRKE.getValue(), arbeidsforhold.getYrke());
            map.put(Headers.ARBEIDSTIDSORDNING.getValue(), arbeidsforhold.getArbeidstidsordning());
            map.put(Headers.STILLINGSPROSENT.getValue(), arbeidsforhold.getStillingsprosent());
            map.put(Headers.SISTE_LOENNSENDRINGSDATO.getValue(), arbeidsforhold.getSisteLoennsendringsdato());
            map.put(Headers.SISTE_DATO_FOR_STILLINGSPROSENTENDRING.getValue(), arbeidsforhold.getSisteDatoForStillingsprosentendring());
            map.put(Headers.PERMISJON_MED_FORELDREPENGER.getValue(), arbeidsforhold.getAntallPermisjonMedForeldrepenger());
            map.put(Headers.PERMITTERING.getValue(), arbeidsforhold.getAntallPermittering());
            map.put(Headers.PERMISJON.getValue(), arbeidsforhold.getAntallPermisjon());
            map.put(Headers.PERMISJON_VED_MILITAERTJENESTE.getValue(), arbeidsforhold.getAntallPermisjonVedMilitaertjeneste());
            map.put(Headers.VELFERDSPERMISJON.getValue(), arbeidsforhold.getAntallVelferdspermisjon());
            map.put(Headers.UTDANNINGSPERMISJON.getValue(), arbeidsforhold.getAntallUtdanningspermisjon());
            return map;
        };
    }

    @Override
    protected CsvHeader[] getHeaders() {
        return Headers.values();
    }
}
