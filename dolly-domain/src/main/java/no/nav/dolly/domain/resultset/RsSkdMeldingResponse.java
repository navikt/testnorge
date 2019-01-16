package no.nav.dolly.domain.resultset;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsSkdMeldingResponse {
    private Long gruppeid;

    private List<SendSkdMeldingTilTpsResponse> sendSkdMeldingTilTpsResponsene;
    private List<ServiceRoutineResponseStatus> serviceRoutineStatusResponsene;
}