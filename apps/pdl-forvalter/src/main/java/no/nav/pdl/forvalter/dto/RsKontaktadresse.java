package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.domain.PdlAdresse;
import no.nav.pdl.forvalter.domain.PdlUtenlandskAdresse;
import no.nav.pdl.forvalter.domain.PdlVegadresse;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsKontaktadresse extends PdlAdresse {

    private PdlVegadresse vegadresse;
    private PdlUtenlandskAdresse utenlandskAdresse;
    private Postboksadresse postboksadresse;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Postboksadresse implements Serializable {
        private String postboks;
        private String postbokseier;
        private String postnummer;
    }
}
