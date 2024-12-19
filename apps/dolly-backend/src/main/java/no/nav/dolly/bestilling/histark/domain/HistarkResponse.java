package no.nav.dolly.bestilling.histark.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistarkResponse {

    private String histarkId;
    private String saksmappeId;
    private String feilmelding;
}
