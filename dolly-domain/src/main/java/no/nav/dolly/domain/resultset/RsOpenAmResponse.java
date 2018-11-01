package no.nav.dolly.domain.resultset;

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
public class RsOpenAmResponse {

    private String miljoe;
    private String jira;
    private String status;
    private int httpCode;
    private String feilmelding;
}
