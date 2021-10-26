package no.nav.dolly.bestilling.dokarkiv.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DokarkivRequest {

    public enum IdType {

        FNR, ORGNR, HPRNR, UTL_ORG
    }

    private String tittel;
    private RsDokarkiv.JournalPostType journalpostType;
    private String tema;
    private String behandlingstema;
    private String kanal;
    private String journalfoerendeEnhet;
    private AvsenderMottaker avsenderMottaker;
    private Bruker bruker;
    private Sak sak;
    private List<Dokument> dokumenter;

    public List<Dokument> getDokumenter() {
        if (isNull(dokumenter)) {
            dokumenter = new ArrayList<>();
            dokumenter.add(new Dokument());
        }
        return dokumenter;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class AvsenderMottaker {

        private String id;
        private IdType idType;
        private String navn;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class Dokument {

        private String tittel;
        private String brevkode;
        private List<DokumentVariant> dokumentvarianter;

        public List<DokumentVariant> getDokumentvarianter() {
            if (isNull(dokumentvarianter)) {
                dokumentvarianter = new ArrayList<>();
                dokumentvarianter.add(new DokumentVariant());
            }
            return dokumentvarianter;
        }

        @Override
        public String toString() {
            return String.format("Dokument{tittel='%s', brevkode='%s', dokumentvariantListe=%s}",
                    tittel,
                    brevkode,
                    dokumentvarianter.stream().map(DokumentVariant::toString).collect(Collectors.toList()));
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class DokumentVariant {

        private String filtype;
        private String fysiskDokument;
        private String variantformat;

        @Override
        public String toString() {
            return "DokumentVariant{" +
                    "filtype='" + filtype + '\'' +
                    ", fysiskDok='" + fysiskDokument.substring(0, 10) + "..." + '\'' +
                    ", variantformat='" + variantformat + '\'' +
                    '}';
        }
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class Bruker {

        private String id;
        private IdType idType;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class Sak {

        private String fagsakId;
        private String fagsaksystem;
        private String sakstype;
    }
}
