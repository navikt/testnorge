package no.nav.testnav.libs.dto.ereg.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Informasjon om organisasjonsnavn")
@JsonPropertyOrder({
        "redigertnavn",
        "navnelinje1",
        "navnelinje2",
        "navnelinje3",
        "navnelinje4",
        "navnelinje5",
        "bruksperiode",
        "gyldighetsperiode"
})
public class Navn {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();

    @Schema(description = "Redigert navn", example = "NAV FAMILIE- OG PENSJONSYTELSER OSL")
    private String redigertnavn;

    @Schema(description = "Navnelinje #1", example = "NAV FAMILIE- OG PENSJONSYTELSER")
    private String navnelinje1;

    @Schema(description = "Navnelinje #2", example = "OSL")
    private String navnelinje2;

    @Schema(description = "Navnelinje #3")
    private String navnelinje3;

    @Schema(description = "Navnelinje #4")
    private String navnelinje4;

    @Schema(description = "Navnelinje #5")
    private String navnelinje5;
}
