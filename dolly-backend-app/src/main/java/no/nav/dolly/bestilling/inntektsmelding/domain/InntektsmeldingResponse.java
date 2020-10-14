package no.nav.dolly.bestilling.inntektsmelding.domain;

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
public class InntektsmeldingResponse {

    private String fnr;
    private List<Dokument> dokumenter;

    public List<Dokument> getDokumenter() {

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
    public static class Dokument {

        private String journalpostId;
        private String dokumentInfoId;
    }
}
