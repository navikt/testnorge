package no.nav.testnav.libs.dto.yrkesskade.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YrkesskadeResponse {

    private List<Sak> saker;

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
