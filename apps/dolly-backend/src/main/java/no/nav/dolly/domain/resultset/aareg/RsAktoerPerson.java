package no.nav.dolly.domain.resultset.aareg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RsAktoerPerson extends RsAktoer {

    @Schema(
            description = "Personident/fødselsnummer")
    private String ident;

    @Schema(
            description = "Gyldige verdier finnes i kodeverk 'Personidenter'")
    private String identtype;

    @Override
    public String getAktoertype() {
        return "PERS";
    }
}
