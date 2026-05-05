package no.nav.dolly.bestilling.kelvinaap.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AapStatusRequest {

    private String ident;
}
