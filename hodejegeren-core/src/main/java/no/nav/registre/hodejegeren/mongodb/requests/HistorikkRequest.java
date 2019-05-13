package no.nav.registre.hodejegeren.mongodb.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import no.nav.registre.hodejegeren.mongodb.Kilde;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorikkRequest {

    private String id;
    private Kilde kilde;
}
