package no.nav.registre.arena.core.consumer.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arbeidsoker {
    private String personident;
    private String miljoe;
    private String status;
    private String eier;
    private Boolean servicebehov;
    private Boolean automatiskInnsendingAvMeldekort;
}
