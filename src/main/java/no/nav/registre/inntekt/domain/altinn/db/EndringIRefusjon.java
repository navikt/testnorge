package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@NoArgsConstructor(force = true)
public class EndringIRefusjon {
    @JsonProperty
    private Long id;
    @JsonProperty
    private LocalDate endringsDato;
    @JsonProperty
    private double refusjonsbeloepPrMnd;
}
