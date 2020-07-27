package no.nav.registre.populasjoner.kafka.folkeregisterperson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PdlDokument {
    Person hentPerson;

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    private static class Person {
        List<Folkeregisteridentifikator> folkeregisteridentifikator;
    }


    @JsonIgnore
    public List<Folkeregisteridentifikator> getFolkeregisteridentifikator() {
        return this.hentPerson != null ? hentPerson.getFolkeregisteridentifikator() : Collections.emptyList();
    }
}
