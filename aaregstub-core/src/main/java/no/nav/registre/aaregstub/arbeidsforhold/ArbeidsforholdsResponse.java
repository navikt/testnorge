package no.nav.registre.aaregstub.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.ArrayList;

import no.nav.registre.aaregstub.arbeidsforhold.contents.Arbeidsforhold;

@Entity(name = "arbeidsforholdsResponse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ArbeidsforholdsResponse {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @JsonProperty("arbeidsforhold")
    @OneToOne(mappedBy = "arbeidsforholdsResponsen", fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    private Arbeidsforhold arbeidsforhold;

    @JsonProperty("arkivreferanse")
    private String arkivreferanse;

    @JsonProperty("environments")
    private ArrayList<String> environments;
}
