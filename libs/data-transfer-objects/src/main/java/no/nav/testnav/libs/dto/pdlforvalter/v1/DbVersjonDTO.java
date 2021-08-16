package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonFilter("idFilter")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class DbVersjonDTO implements Serializable {

    public enum Master {FREG, PDL}

    @Schema(description = "Versjon av informasjonselement. Fravær av denne eller 0 betyr nytt element")
    private Integer id;

    @Schema(defaultValue = "Dolly",
            description = "Dataens opprinnelse")
    private String kilde;

    @Schema(defaultValue = "FREG",
            description = "Hvem er master, FREG eller PDL?")
    private Master master;

    @JsonIgnore
    private Boolean isNew;
}
