package no.nav.dolly.bestilling.inntektsmelding.domain;

import lombok.*;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;

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

    public static Flux<InntektsmeldingResponse> of(WebClientError.Description description, String fnr, String miljoe) {
        return Flux.just(InntektsmeldingResponse
                .builder()
                .fnr(fnr)
                .status(description.getStatus())
                .error(description.getMessage())
                .miljoe(miljoe)
                .build());
    }

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
