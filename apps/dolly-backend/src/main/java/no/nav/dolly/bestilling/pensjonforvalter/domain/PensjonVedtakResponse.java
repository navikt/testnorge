package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensjonVedtakResponse {

    public enum SakType {AP, UT}

    private String sakId;
    private SakType sakType;
    private String vedtakStatus;
    private String sisteOppdatering;
}
