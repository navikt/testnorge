package no.nav.dolly.bestilling.dokarkiv.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DokarkivResponse {

    private String journalpostId;
    private boolean journalpostferdigstilt;
    private List<DokumentInfo> dokumenter;

    private String dokument;

    private String feilmelding;
    private String miljoe;

    public static Mono<DokarkivResponse> of(WebClientError.Description description, String environment) {
        return Mono.just(DokarkivResponse
                .builder()
                .feilmelding(description.getMessage())
                .miljoe(environment)
                .build());
    }

    public List<DokumentInfo> getDokumenter() {
        if (isNull(dokumenter)) {
            dokumenter = new ArrayList<>();
        }
        return dokumenter;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DokumentInfo {

        private String dokumentInfoId;
    }
}
