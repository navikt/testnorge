package no.nav.testnav.libs.dto.pdlforvalter.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SikkerhetstiltakDTO extends DbVersjonDTO {

    public enum Tiltakstype {FYUS, TFUS, FTUS, DIUS, TOAN}

    @Schema(required = true)
    private Tiltakstype tiltakstype;

    @Schema(required = true)
    private String beskrivelse;

    private Kontaktperson kontaktperson;

    @Schema(required = true)
    private LocalDateTime gyldigFraOgMed;

    @Schema(required = true)
    private LocalDateTime gyldigTilOgMed;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kontaktperson implements Serializable {

        @Schema(description = "NAV-ident, (bokstav + 6 sifre", required = true, example = "Z9999999")
        private String personident;

        @Schema(description = "NAV-enhet (4 sifre)", required = true)
        private String enhet;
    }
}