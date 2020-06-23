package no.nav.registre.sdForvalter.consumer.rs.request.aareg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import no.nav.registre.sdForvalter.domain.Aareg;

@Getter
@Setter
@AllArgsConstructor
public class Arbeidsforhold {

    public Arbeidsforhold(Aareg aareg) {
        this.arbeidsgiver = new Arbeidsgiver(String.valueOf(aareg.getOrgId()));
        this.arbeidstaker = new Arbeidstaker(aareg.getFnr());
    }

    @JsonProperty("arbeidsgiver")
    private Arbeidsgiver arbeidsgiver;

    @JsonProperty("arbeidstaker")
    private Arbeidstaker arbeidstaker;

}
