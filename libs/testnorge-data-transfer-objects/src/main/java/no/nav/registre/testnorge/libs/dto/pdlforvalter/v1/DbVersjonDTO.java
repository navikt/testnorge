package no.nav.registre.testnorge.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public abstract class DbVersjonDTO implements Serializable {

    @Schema(description = "Versjon av informasjonselement. Fravær av denne eller 0 betyr nytt element")
    private Integer id;

    @Schema(defaultValue = "Dolly",
            description = "Dataens opprinnelse")
    private String kilde;

    @JsonIgnore
    private Boolean isNew;
}
