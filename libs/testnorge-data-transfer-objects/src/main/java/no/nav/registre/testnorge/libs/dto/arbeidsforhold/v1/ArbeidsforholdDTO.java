package no.nav.registre.testnorge.libs.dto.arbeidsforhold.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdDTO {
    @JsonProperty
    String arbeidsforholdId;
    @JsonProperty(required = true)
    String orgnummer;
    @JsonProperty
    String arbeidstidsordning;
    @JsonProperty(required = true)
    Double stillingsprosent;
    @JsonProperty(required = true)
    String yrke;
    @JsonProperty(required = true)
    LocalDate fom;
    @JsonProperty(required = true)
    LocalDate tom;
    @JsonProperty(required = true)
    String ident;
}
