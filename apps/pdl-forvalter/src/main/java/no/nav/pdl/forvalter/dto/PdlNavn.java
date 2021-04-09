package no.nav.pdl.forvalter.dto;

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
public class PdlNavn {

    private String etternavn;
    private String forkortetNavn;
    private String fornavn;
    private String kilde;
    private String mellomnavn;
    private OriginaltNavn originaltNavn;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class OriginaltNavn {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
    }
}
