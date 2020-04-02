package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@NoArgsConstructor(force = true)
public class NaturalytelseDetaljer {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String type;
    @JsonProperty
    private LocalDate fom;
    @JsonProperty
    private double beloepPrMnd;
}
