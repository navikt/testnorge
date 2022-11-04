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
@Schema(description = "Informasjon om person (arbeidstaker/arbeidsgiver/opplysningspliktig)")
public class Person extends OpplysningspliktigArbeidsgiver implements Persontype {

    @Schema(description = "Gjeldende offentlig ident", example = "31126700000")
    private String offentligIdent;

    @Schema(description = "Aktør-id", example = "1234567890")
    private String aktoerId;

    @Override
    @JsonIgnore
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
