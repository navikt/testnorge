package no.nav.registre.aaregstub.arbeidsforhold.contents;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

import no.nav.registre.aaregstub.arbeidsforhold.ArbeidsforholdsResponse;
import no.nav.registre.aaregstub.arbeidsforhold.Ident;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Arbeidsforhold {

    @ManyToOne
    @JoinColumn(name = "ident_fnr")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIgnore
    private Ident identen;

    @OneToOne
    @JoinColumn(name = "arbeidsforholdsResponse_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIgnore
    private ArbeidsforholdsResponse arbeidsforholdsResponsen;

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @Embedded
    @JsonProperty("ansettelsesPeriode")
    private AnsettelsesPeriode ansettelsesPeriode;

    @JsonProperty("antallTimerForTimeloennet")
    @OneToMany(mappedBy = "arbeidsforholdet", cascade = { CascadeType.ALL })
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AntallTimerForTimeloennet> antallTimerForTimeloennet;

    @JsonProperty("arbeidsavtale")
    @OneToOne(mappedBy = "arbeidsforholdet", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    private Arbeidsavtale arbeidsavtale;

    @JsonProperty("arbeidsforholdID")
    private String arbeidsforholdID;

    @JsonProperty("arbeidsforholdIDnav")
    private Integer arbeidsforholdIDnav;

    @JsonProperty("arbeidsforholdstype")
    private String arbeidsforholdstype;

    @Embedded
    @JsonProperty("arbeidsgiver")
    private Arbeidsgiver arbeidsgiver;

    @Embedded
    @JsonProperty("arbeidstaker")
    private Arbeidstaker arbeidstaker;

    @JsonProperty("permisjon")
    @OneToMany(mappedBy = "arbeidsforholdet", cascade = { CascadeType.ALL })
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Permisjon> permisjon;

    @JsonProperty("utenlandsopphold")
    @OneToMany(mappedBy = "arbeidsforholdet", cascade = { CascadeType.ALL })
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Utenlandsopphold> utenlandsopphold;
}
