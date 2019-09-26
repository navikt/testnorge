package no.nav.registre.arena.core.consumer.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArbeidsoekerStatus {
    String personident;
    String miljoe;
    String status;
}
