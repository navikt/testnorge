package no.nav.registre.testnorge.levendearbeidsforhold.domain.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "periode",
        "sluttaarsak",
        "varslingskode",
        "bruksperiode",
        "sporingsinformasjon"
})
@Schema(description = "Informasjon knyttet til ansettelsesperioden")
public class Ansettelsesperiode {

    private Periode periode;

    @Schema(description = "&Aring;rsak for avsluttet ansettelsesperiode (kodeverk: Slutt%C3%A5rsakAareg)", example = "arbeidstakerHarSagtOppSelv")
    private String sluttaarsak;

    @Schema(description = "Varslingskode (kodeverk: Varslingskode_5fAa-registeret) - benyttes hvis ansettelsesperiode er lukket maskinelt", example = "ERKONK")
    private String varslingskode;

    private Bruksperiode bruksperiode;

    private Sporingsinformasjon sporingsinformasjon;

    @Override
    public String toString() {
        return ("Ansettelsesforhold: [" + periode.toString() + ", " + sluttaarsak + ", " + varslingskode);
    }
}
