package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private String type;

    @Override
    @JsonIgnore
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
