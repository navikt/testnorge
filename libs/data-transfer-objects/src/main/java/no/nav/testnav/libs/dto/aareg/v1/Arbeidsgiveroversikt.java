package no.nav.testnav.libs.dto.aareg.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Informasjon knyttet til arbeidsgiver (overordnet)")
public class Arbeidsgiveroversikt {

    @Schema(description = "Arbeidsgiver - organisasjon eller person")
    private OpplysningspliktigArbeidsgiver arbeidsgiver;

    @Schema(description = "Antall aktive arbeidsforhold, dvs. de som har en gjeldende ansettelsesperiode")
    private Integer aktiveArbeidsforhold;

    @Schema(description = "Antall inaktive arbeidsforhold, dvs. de som har en historisk ansettelsesperiode")
    private Integer inaktiveArbeidsforhold;

}
