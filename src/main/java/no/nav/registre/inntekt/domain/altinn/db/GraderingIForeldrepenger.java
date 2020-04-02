package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class GraderingIForeldrepenger {
    @JsonProperty
    Long id;
    @JsonProperty
    Periode periode;
    @JsonProperty
    Integer gradering;
}
