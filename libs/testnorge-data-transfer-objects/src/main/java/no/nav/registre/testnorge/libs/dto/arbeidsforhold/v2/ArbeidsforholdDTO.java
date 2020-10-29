package no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdDTO {
    @JsonProperty
    String arbeidsforholdId;
    @JsonProperty
    String typeArbeidsforhold;
    @JsonProperty(required = true)
    LocalDate startdato;
    @JsonProperty(required = true)
    LocalDate sluttdato;
    @JsonProperty
    Float antallTimerPerUke;
    @JsonProperty(required = true)
    String yrke;
    @JsonProperty
    String arbeidstidsordning;
    @JsonProperty
    Float stillingsprosent;
    @JsonProperty
    LocalDate sisteLoennsendringsdato;
}
