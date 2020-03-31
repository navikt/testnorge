package no.nav.registre.inntekt.domain.altinn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AltinnInntektInfo {
    @JsonProperty
    private String virksomhetsnummer;
    @JsonProperty
    private LocalDate dato;
    @JsonProperty
    private Double beloep;
}
