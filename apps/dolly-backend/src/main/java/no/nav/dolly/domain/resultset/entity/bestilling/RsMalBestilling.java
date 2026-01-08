package no.nav.dolly.domain.resultset.entity.bestilling;

import tools.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsMalBestilling {

    private Long id;
    private String malNavn;
    private JsonNode malBestilling;
    private String miljoer;
    private LocalDateTime sistOppdatert;
}
