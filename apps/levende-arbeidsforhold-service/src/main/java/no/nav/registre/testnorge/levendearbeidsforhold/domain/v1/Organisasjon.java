package no.nav.registre.testnorge.levendearbeidsforhold.domain.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "organisasjonsnummer"
})
@Schema(description = "Informasjon om organisasjon (arbeidsgiver/opplysningspliktig)")
public class Organisasjon extends OpplysningspliktigArbeidsgiver implements OpplysningspliktigArbeidsgiverType {

    @Schema(description = "Organisasjonsnummer fra Enhetsregisteret", example = "987654321")
    private String organisasjonsnummer;

    @Override
    @JsonIgnore
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
