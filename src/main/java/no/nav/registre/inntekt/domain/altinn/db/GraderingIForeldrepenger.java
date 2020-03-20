package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GraderingIForeldrepenger {
    @JsonProperty
    private Long id;
    @JsonProperty
    private Periode periode;
    @JsonProperty
    private Integer gradering;
}
