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
@Schema(description = "Arbeidsavtale/ansettelsesdetaljer for ordin&aelig;re arbeidsforhold")
public class OrdinaerArbeidsavtale extends Arbeidsavtale {

    public static final String TYPE = "Ordinaer";

    @Override
    @JsonIgnore
    public String getType() {
        return TYPE;
    }
}
