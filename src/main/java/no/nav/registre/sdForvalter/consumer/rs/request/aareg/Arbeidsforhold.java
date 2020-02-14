package no.nav.registre.sdForvalter.consumer.rs.request.aareg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import no.nav.registre.sdForvalter.database.model.AaregModel;

@Getter
@Setter
@AllArgsConstructor
public class Arbeidsforhold {

    public Arbeidsforhold(AaregModel aaregModel) {
        this.arbeidsgiver = new Arbeidsgiver(String.valueOf(aaregModel.getOrgId()));
        this.arbeidstaker = new Arbeidstaker(aaregModel.getFnr());
    }

    @JsonProperty("arbeidsgiver")
    private Arbeidsgiver arbeidsgiver;

    @JsonProperty("arbeidstaker")
    private Arbeidstaker arbeidstaker;

}
