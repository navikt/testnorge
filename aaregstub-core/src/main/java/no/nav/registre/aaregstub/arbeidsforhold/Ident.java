package no.nav.registre.aaregstub.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

import no.nav.registre.aaregstub.arbeidsforhold.contents.Arbeidsforhold;

@Entity(name = "ident")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Ident {

    @Id
    private String fnr;

    @OneToMany(mappedBy = "identen", fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    private List<Arbeidsforhold> arbeidsforhold;
}
