package no.nav.testnav.libs.dto.hendelse.v1;

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
public class HendelseDTO {
    @JsonProperty(required = true)
    String ident;
    @JsonProperty(required = true)
    LocalDate fom;
    @JsonProperty
    LocalDate tom;
    @JsonProperty(required = true)
    HendelseType type;
}
