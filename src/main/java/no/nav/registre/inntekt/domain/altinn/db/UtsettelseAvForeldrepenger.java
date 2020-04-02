package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class UtsettelseAvForeldrepenger {
    @JsonProperty
    private Long id;
    @JsonProperty
    private Periode periode;
    @JsonProperty
    private String aarsakTilUtsettelse;
}
