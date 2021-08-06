package no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AldersUfoereEtterlatteAvtalefestetOgKrigspensjon {

    private Double grunnpensjonsbeloep;
    private Double tilleggspensjonsbeloep;
    private Integer ufoeregrad;
    private Integer pensjonsgrad;
    private Double heravEtterlattepensjon;
    private Periode tidsrom;
}
