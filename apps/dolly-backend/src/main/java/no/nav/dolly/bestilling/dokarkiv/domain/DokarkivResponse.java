package no.nav.dolly.bestilling.dokarkiv.domain;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DokarkivResponse {

    private String journalpostId;
    private boolean journalpostferdigstilt;
    private List<DokumentInfo> dokumenter;

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
