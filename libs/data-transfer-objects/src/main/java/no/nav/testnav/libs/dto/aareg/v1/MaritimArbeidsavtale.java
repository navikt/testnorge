package no.nav.testnav.libs.dto.aareg.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Arbeidsavtale/ansettelsesdetaljer for maritime arbeidsforhold")
public class MaritimArbeidsavtale extends Arbeidsavtale {

    public static final String TYPE = "Maritim";

    @Schema(description = "Fartsomr&aring;de (kodeverk: Fartsomraader)", example = "utenriks")
    private String fartsomraade;

    @Schema(description = "Skipsregister (kodeverk: Skipsregistre)", example = "nis")
    private String skipsregister;

    @Schema(description = "Skipstype (kodeverk: Skipstyper)", example = "turist")
    private String skipstype;

    @Override
    @JsonIgnore
    public String getType() {
        return TYPE;
    }
}
