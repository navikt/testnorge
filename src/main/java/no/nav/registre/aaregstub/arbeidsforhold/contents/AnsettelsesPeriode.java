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
public class AnsettelsesPeriode {

    @JsonProperty("fom")
    @Column(name = "ANSETTELSESPERIODE_FOM")
    private String fom;

    @JsonProperty("tom")
    @Column(name = "ANSETTELSESPERIODE_TOM")
    private String tom;
}
