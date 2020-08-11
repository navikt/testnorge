package no.nav.dolly.bestilling.sykemelding.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class LegeDTO {
    @JsonProperty
    private String fnr;
    @JsonProperty
    private String fornavn;
    @JsonProperty
    private String mellomnavn;
    @JsonProperty
    private String etternavn;
    @JsonProperty
    private String hprId;
}

