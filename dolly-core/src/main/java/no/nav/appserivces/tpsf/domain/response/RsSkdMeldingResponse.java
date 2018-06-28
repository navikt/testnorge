package no.nav.appserivces.tpsf.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsSkdMeldingResponse {
    private Long gruppeid;

    List<SendSkdMeldingTilTpsResponse> sendSkdMeldingTilTpsResponsene;
    List<ServiceRoutineResponseStatus> serviceRoutineStatusResponsene;

}