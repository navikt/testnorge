package no.nav.dolly.domain.resultset.entity.bestilling;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsMalBestilling {

    private Long id;
    private String malNavn;
    private JsonNode malBestilling;
    private String miljoer;
    private String sistOppdatert;
}
