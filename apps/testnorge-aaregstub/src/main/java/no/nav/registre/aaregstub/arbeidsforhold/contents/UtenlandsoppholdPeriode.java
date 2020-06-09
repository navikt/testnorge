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
public class UtenlandsoppholdPeriode {

    @JsonProperty("fom")
    @Column(name = "UTENLANDSOPPHOLDPERIODE_FOM")
    private String fom;

    @JsonProperty("tom")
    @Column(name = "UTENLANDSOPPHOLDPERIODE_TOM")
    private String tom;
}
