package no.nav.dolly.bestilling.histark.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistarkResponse {

    private String id;
    private HttpStatus status;
    private String feilmelding;

    public boolean isOk() {

        return isBlank(feilmelding);
    }
}
