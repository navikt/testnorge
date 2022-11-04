package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpprettRequest {

    private DbPerson person;
    private boolean skalSlettes;
}
