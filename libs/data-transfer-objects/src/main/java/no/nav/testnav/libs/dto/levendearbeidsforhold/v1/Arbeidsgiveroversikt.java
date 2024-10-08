package no.nav.testnav.libs.dto.levendearbeidsforhold.v1;

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
