package no.nav.dolly.domain.resultset.tpsf;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsPartnerRelasjonRequest {

    @Schema(required = true,
            description = "Entydig identifisering av partner")
    private String ident;

    @Schema(description = "Liste av sivilstander beskriver forhold mellom hovedperson og partner")
    private List<RsSivilstandRequest> sivilstander;

    @Schema(description = "Når true (eller blankt) får partner samme adresse som hovedperson. False innebærer ulik boadresse.")
    private Boolean harFellesAdresse;
}