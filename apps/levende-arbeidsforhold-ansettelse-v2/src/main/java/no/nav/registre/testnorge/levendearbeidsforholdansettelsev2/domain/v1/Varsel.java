package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.domain.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "entitet",
        "type",
        "varslingskode"
})
@Schema(description = "Informasjon om varsel")
public class Varsel {

    @Schema(description = "Entitet for varsel")
    private Varselentitet entitet;

    @Schema(description = "Varslingskode (kodeverk: Varslingskode_5fAa-registeret)")
    private String varslingskode;
}
