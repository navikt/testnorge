package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType;

import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpprettRequest {

    private DbPerson person;

    public boolean isNotTestnorgeIdent() {

        return !isTestnorgeIdent(person.getIdent());
    }

    @JsonIgnore
    public boolean noneAlias() {
        return getPerson().getRelasjoner().stream()
                .map(DbRelasjon::getRelasjonType)
                .noneMatch(type -> type == RelasjonType.NY_IDENTITET);
    }
}