package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

import static java.util.Objects.nonNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class DbVersjonDTO implements Serializable {

    @Schema(description = "Versjon av informasjonselement. Fravær av denne eller 0 betyr nytt element")
    private Integer id;

    @Schema(defaultValue = "Dolly",
            description = "Dataens opprinnelse")
    private String kilde;

    @Schema(defaultValue = "FREG",
            description = "Hvem er master, FREG eller PDL?")
    private Master master;

    @Schema(defaultValue = "true",
            description = "true = gjeldende informasjon, false = historisk")
    private Boolean gjeldende;

    @JsonIgnore
    private Boolean isNew;

    @Schema(description = "Denne kan også benyttes ved behov")
    private FolkeregistermetadataDTO metadata;

    @JsonIgnore
    protected static <T> int count(T artifact) {
        return nonNull(artifact) ? 1 : 0;
    }

    public enum Master {FREG, PDL}
}
