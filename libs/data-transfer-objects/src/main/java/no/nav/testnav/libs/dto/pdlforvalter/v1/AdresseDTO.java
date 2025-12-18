package no.nav.testnav.libs.dto.pdlforvalter.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AdresseDTO extends DbVersjonDTO {

    private String adresseIdentifikatorFraMatrikkelen;
    
    private LocalDateTime gyldigFraOgMed;
    
    private LocalDateTime gyldigTilOgMed;

    @Schema(description = "For å sette coAdresseNavn, benytt opprettCoAdresseNavn")
    private String coAdressenavn;

    private CoNavnDTO opprettCoAdresseNavn;

    public abstract boolean isAdresseNorge();

    public abstract boolean isAdresseUtland();

    @JsonDeserialize(using = OppholdAnnetStedEnumDeserializer.class)
    public enum OppholdAnnetSted {MILITAER, UTENRIKS, PAA_SVALBARD, PENDLER}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoNavnDTO implements Serializable {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
        private Boolean hasMellomnavn;
    }
}
