package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonFilter("idFilter")
public abstract class PdlDbVersjon implements Serializable {

    @Schema(required = true,
            description = "Versjon av informasjonselement")
    private Integer id;
    @Schema(defaultValue = "Dolly",
            description = "Dataens opprinnelse")
    private String kilde;

}
