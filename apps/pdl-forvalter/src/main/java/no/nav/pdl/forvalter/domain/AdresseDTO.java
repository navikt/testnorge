package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AdresseDTO extends DbVersjonDTO {

    private String adresseIdentifikatorFraMatrikkelen;
    private LocalDateTime gyldigFraOgMed;
    private LocalDateTime gyldigTilOgMed;
    private String coAdressenavn;

    @Schema(defaultValue = "FREG")
    private Master master;

    public enum Master {FREG, PDL}

    public enum OppholdAnnetSted {MILITAER, UTENRIKS, PAA_SVALBARD, PENDLER}
}
