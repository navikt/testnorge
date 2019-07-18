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
public class Arbeidsgiver {

    @JsonProperty("aktoertype")
    @Column(name = "ARBEIDSGIVER_AKTOERTYPE")
    private String aktoertype;

    @JsonProperty("orgnummer")
    @Column(name = "ARBEIDSGIVER_ORGNUMMEr")
    private String orgnummer;
}
