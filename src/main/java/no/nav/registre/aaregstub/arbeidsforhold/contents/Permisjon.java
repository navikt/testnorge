package no.nav.registre.aaregstub.arbeidsforhold.contents;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permisjon {

    @ManyToOne
    @JoinColumn(name = "arbeidsforhold_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIgnore
    private Arbeidsforhold arbeidsforholdet;

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @JsonProperty("permisjonOgPermittering")
    private String permisjonOgPermittering;

    @JsonProperty("permisjonsId")
    private String permisjonsId;

    @Embedded
    @JsonProperty("permisjonsPeriode")
    private PermisjonsPeriode permisjonsPeriode;

    @JsonProperty("permisjonsprosent")
    private Double permisjonsprosent;
}
