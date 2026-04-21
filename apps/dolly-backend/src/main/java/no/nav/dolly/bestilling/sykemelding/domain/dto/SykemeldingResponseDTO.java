package no.nav.dolly.bestilling.sykemelding.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.sykemelding.SykmeldingType;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SykemeldingResponseDTO {

    private HttpStatus status;
    private String avvik;
    private String sykmeldingId;
    private SykmeldingType type;
    private List<Aktivitet> aktivitet;
    private SykemeldingRequestDTO sykemeldingRequest;
    private String ident;

    public static Mono<SykemeldingResponseDTO> of(WebClientError.Description description, String ident) {
        return Mono.just(SykemeldingResponseDTO
                .builder()
                .ident(ident)
                .status(description.getStatus())
                .avvik(description.getMessage())
                .build());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Aktivitet {

        private Integer grad;
        private Boolean reisetilskudd;
        private LocalDate fom;
        private LocalDate tom;
    }
}
