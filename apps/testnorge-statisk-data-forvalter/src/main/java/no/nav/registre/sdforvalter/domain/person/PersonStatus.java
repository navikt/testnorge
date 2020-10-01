package no.nav.registre.sdforvalter.domain.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class PersonStatus {
    @JsonProperty
    Person fraFasteData;
    @JsonProperty
    Person fraPDL;

    @JsonIgnore
    String getFnr() {
        return fraFasteData.getFnr();
    }

    @JsonProperty
    public boolean isEqual() {
        if (fraPDL == null) {
            return false;
        }
        return fraPDL.equals(fraFasteData);
    }
}