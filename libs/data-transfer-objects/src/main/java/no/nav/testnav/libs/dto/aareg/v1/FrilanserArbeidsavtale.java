package no.nav.testnav.libs.dto.aareg.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Arbeidsavtale/ansettelsesdetaljer for frilanser arbeidsforhold")
public class FrilanserArbeidsavtale extends Arbeidsavtale {

    public static final String TYPE = "Frilanser";

    @Override
    @JsonIgnore
    public String getType() {
        return TYPE;
    }
}
