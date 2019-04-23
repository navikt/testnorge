package no.nav.registre.aaregstub.arbeidsforhold.contents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AntallTimerForTimeloennetPeriode {

    @JsonProperty("fom")
    @Column(name = "ANTALLTIMERFORTIMELOENNETPERIODE_FOM")
    private String fom;

    @JsonProperty("tom")
    @Column(name = "ANTALLTIMERFORTIMELOENNETPERIODE_TOM")
    private String tom;
}
