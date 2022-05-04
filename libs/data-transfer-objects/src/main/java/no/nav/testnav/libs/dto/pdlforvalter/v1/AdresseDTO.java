package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.pdlforvalter.v1.deserialization.OppholdAnnetStedEnumDeserializer;

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

    public abstract boolean isAdresseNorge();

    @JsonIgnore
    public boolean isAdresseUtland() {

        return !isAdresseNorge();
    }
}
