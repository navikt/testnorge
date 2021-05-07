package no.nav.pdl.forvalter.domain;

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
public class PdlTelefonnummer extends PdlDbVersjon {

    private String kilde;
    private String landskode;
    private String nummer;
    private Integer prioritet;
}
