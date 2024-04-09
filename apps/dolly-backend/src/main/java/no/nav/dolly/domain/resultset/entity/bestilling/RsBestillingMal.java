package no.nav.dolly.domain.resultset.entity.bestilling;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.Bruker;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsBestillingMal {

    private Long id;
    private JsonNode bestKriterier;
    private String miljoer;
    private String malNavn;
    private Bruker bruker;
    private LocalDateTime sistOppdatert;
}
