package no.nav.organisasjonforvalter.dto.responses.ereg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "enhetstype",
        "sektorkode"
})
public class OrganisasjonsleddDetaljer {

    @Schema(description = "Enhetstype (kodeverk)", example = "ORGL")
    private String enhetstype;

    @Schema(description = "Sektorkode (kodeverk: Sektorkoder)", example = "6500")
    private String sektorkode;
}
