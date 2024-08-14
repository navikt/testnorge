package no.nav.registre.testnorge.levendearbeidsforhold.domain.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "arbeidsgiver",
        "aktiveArbeidsforhold",
        "inaktiveArbeidsforhold"
})
@Schema(description = "Informasjon knyttet til arbeidsgiver (overordnet)")
public class Arbeidsgiveroversikt {

    private OpplysningspliktigArbeidsgiver arbeidsgiver;

    @Schema(description = "Antall aktive arbeidsforhold, dvs. de som har en gjeldende ansettelsesperiode")
    private Integer aktiveArbeidsforhold;

    @Schema(description = "Antall inaktive arbeidsforhold, dvs. de som har en historisk ansettelsesperiode")
    private Integer inaktiveArbeidsforhold;

}
