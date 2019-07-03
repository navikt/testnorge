package no.nav.registre.arena.core.consumer.rs.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Arbeidsoker {
    @JsonProperty
    private String personident;
    @JsonProperty
    private String miljoe;
    @JsonProperty
    private String status; // OK
    @JsonProperty
    private String eier;
    @JsonProperty
    private Boolean servicebehov; // true
    @JsonProperty
    private Boolean automatiskInnsendingAvMeldekort; // true
}
