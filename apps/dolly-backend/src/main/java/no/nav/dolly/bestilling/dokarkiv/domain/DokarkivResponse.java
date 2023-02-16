package no.nav.dolly.bestilling.dokarkiv.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DokarkivResponse {

    private String journalpostId;
    private boolean journalpostferdigstilt;
    private List<DokumentInfo> dokumenter;

    private String feilmelding;
    private String miljoe;

    public List<DokumentInfo> getDokumenter() {
        if (isNull(dokumenter)) {
            dokumenter = new ArrayList<>();
        }
        return dokumenter;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DokumentInfo {

        private String dokumentInfoId;
        private String brevkode;
        private String tittel;
    }
}
