package no.nav.dolly.bestilling.inntektsmelding.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InntektsmeldingResponse {

    private String fnr;
    private List<Dokument> dokumenter;
    private HttpStatus status;
    private String error;
    private String miljoe;

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
