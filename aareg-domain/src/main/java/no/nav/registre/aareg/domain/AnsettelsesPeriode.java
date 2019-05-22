package no.nav.registre.aareg.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnsettelsesPeriode {

    @JsonProperty("fom")
    private String fom;

    @JsonProperty("tom")
    private String tom;
}
