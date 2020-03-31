package no.nav.registre.inntekt.domain.dokmot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bruker {
    @JsonProperty
    private String id;
    @JsonProperty
    private String idType;
}
