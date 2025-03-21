package no.nav.dolly.bestilling.yrkesskade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaksoversiktDTO {

    private HttpStatusCode status;
    private String melding;

    private List<Sak> saker;

    public static Mono<SaksoversiktDTO> of(WebClientError.Description description) {
        return Mono.just(SaksoversiktDTO
                .builder()
                .status(description.getStatus())
                .melding(description.getMessage())
                .build());
    }

    public List<Sak> getSaker() {

        if (Objects.isNull(saker)) {
            saker = new ArrayList<>();
        }
        return saker;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sak {

        private LocalDate mottattdato;
        private LocalDate skadedato;
        private LocalDate vedtaksdato;
        private String sakstype;
        private String kommunenr;
        private String saksnr;
        private String resultat;
        private String resultattekst;
        private String kildesystem;
    }
}