package no.nav.registre.testnorge.levendearbeidsforhold.domain.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "offentligIdent",
        "aktoerId"
})
@Schema(description = "Informasjon om person (arbeidstaker/arbeidsgiver/opplysningspliktig)")
public class Person extends OpplysningspliktigArbeidsgiver implements Persontype {

    @Schema(description = "Gjeldende offentlig ident", example = "31126700000")
    private String offentligIdent;

    @Schema(description = "Akt&oslash;r-id", example = "1234567890")
    private String aktoerId;

    @Override
    @JsonIgnore
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
