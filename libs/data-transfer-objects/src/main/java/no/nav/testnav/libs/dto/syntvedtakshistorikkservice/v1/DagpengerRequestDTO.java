package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DagpengerRequestDTO {

    private String personident;
    private String miljoe;
    private List<DagpengesoknadDTO> nyeMottaDagpengesoknad;
    private List<DagpengevedtakDTO> nyeMottaDagpengevedtak;
}
