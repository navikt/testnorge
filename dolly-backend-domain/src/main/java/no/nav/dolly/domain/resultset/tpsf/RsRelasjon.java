package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsRelasjon extends RsTpsfBasisBestilling{

    @ApiModelProperty(
            position = 6,
            value = "Identtype FNR/DNR/BOST, default er FNR"
    )
    private String identtype;

    @ApiModelProperty(
            position = 7,
            value = "Født etter denne dato. Dafault velges voksne personer i alder 30-60, mens barn får alder 0-18"
    )
    private LocalDateTime foedtEtter;

    @ApiModelProperty(
            position = 8,
            value = "Født før denne dato. Dafault velges voksne personer i alder 30-60, mens barn får alder 0-18"
    )
    private LocalDateTime foedtFoer;

    @ApiModelProperty(
            position = 9,
            value = "Kjønn på testperson. Gyldige verdier: 'K', 'M' og 'U'. Ubestemt betyr at systemet velger for deg og generert person blir mann eller kvinne"
    )
    private String kjonn;

    @ApiModelProperty(
            position = 100
    )
    private List<RsIdenthistorikk> identHistorikk;
}
