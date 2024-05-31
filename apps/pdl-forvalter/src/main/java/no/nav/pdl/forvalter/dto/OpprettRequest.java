package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;

import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpprettRequest {

    private DbPerson person;

    @JsonIgnore
    public boolean isNotTestnorgeIdent() {

        return !isTestnorgeIdent(person.getIdent());
    }
}