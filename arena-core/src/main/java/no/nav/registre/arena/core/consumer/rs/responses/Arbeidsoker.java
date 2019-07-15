package no.nav.registre.arena.core.consumer.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Arbeidsoker {
    private String personident;
    private String miljoe;
    private String status; // OK
    private String eier;
    private Boolean servicebehov; // true
    private Boolean automatiskInnsendingAvMeldekort; // true
}
