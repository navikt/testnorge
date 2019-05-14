package no.nav.registre.hodejegeren.mongodb.requests;

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
public class HistorikkRequest {

    private String id;
    private KildeRequest kilde;
}
