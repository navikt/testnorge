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
public class Arbeidstaker {

    @JsonProperty("aktoertype")
    @Column(name = "ARBEIDSTAKER_AKTOERTYPE")
    private String aktoertype;

    @JsonProperty("ident")
    @Column(name = "ARBEIDSTAKER_IDENT")
    private String ident;

    @JsonProperty("identtype")
    @Column(name = "ARBEIDSTAKER_IDENTTYPE")
    private String identtype;
}
