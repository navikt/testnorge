package no.nav.registre.hodejegeren.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorikkRequest {

    private String kilde;
    private List<DataRequest> identMedData;
}
