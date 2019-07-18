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
public class PermisjonsPeriode {

    @JsonProperty("fom")
    @Column(name = "PERMISJONSPERIODE_FOM")
    private String fom;

    @JsonProperty("tom")
    @Column(name = "PERMISJONSPERIODE_TOM")
    private String tom;
}
