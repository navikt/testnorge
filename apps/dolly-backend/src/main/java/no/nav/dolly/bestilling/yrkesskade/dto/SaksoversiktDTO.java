package no.nav.dolly.bestilling.yrkesskade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

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