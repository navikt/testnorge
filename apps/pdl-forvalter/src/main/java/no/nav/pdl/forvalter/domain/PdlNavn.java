package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlNavn extends PdlDbVersjon {

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
    private static class OriginaltNavn implements Serializable {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
    }
}
