package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@NoArgsConstructor(force = true)
public class EndringIRefusjon {
    @JsonProperty
    Long id;
    @JsonProperty
    LocalDate endringsDato;
    @JsonProperty
    double refusjonsbeloepPrMnd;
}
