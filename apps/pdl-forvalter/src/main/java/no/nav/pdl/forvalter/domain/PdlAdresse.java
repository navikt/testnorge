package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PdlAdresse extends PdlDbVersjon {

    private String adresseIdentifikatorFraMatrikkelen;
    private LocalDateTime gyldigFraOgMed;
    private LocalDateTime gyldigTilOgMed;
    private String coAdressenavn;

    @Schema(defaultValue = "Dolly")
    private String kilde;

    @Schema(defaultValue = "FREG")
    private Master master;

    private String naerAdresseIdentifikatorFraMatrikkelen;

    public enum Bruksenhetstype {BOLIG, ANNET_ENN_BOLIG, FRITIDSBOLIG, IKKE_GODKJENT_BOLIG, UNUMMERERT_BRUKSENHET}

    public enum Master {FREG, PDL}

    public enum OppholdAnnetSted {MILITAER, UTENRIKS, PAA_SVALBARD, PENDLER}
}
