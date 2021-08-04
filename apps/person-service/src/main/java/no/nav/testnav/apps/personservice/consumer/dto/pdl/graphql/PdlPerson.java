package no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PdlPerson {
    List<Feilmelding> errors;
    Data data;

    public List<Feilmelding> getErrors() {
        return errors == null ? Collections.emptyList() : errors;
    }
}
