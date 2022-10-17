package no.nav.testnav.libs.dto.aareg.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
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
