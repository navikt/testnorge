package no.nav.dolly.domain.resultset.aareg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAktoerPerson extends RsAktoer {

    @Schema(required = true,
            description = "Personident/fødselsnummer")
    private String ident;

    @Schema(required = true,
            description = "Gyldige verdier finnes i kodeverk 'Personidenter'")
    private String identtype;

    @Override
    public String getAktoertype() {
        return "PERS";
    }
}
