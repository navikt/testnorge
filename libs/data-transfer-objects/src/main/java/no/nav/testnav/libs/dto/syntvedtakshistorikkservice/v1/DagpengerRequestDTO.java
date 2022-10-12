package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DagpengerRequestDTO {

    private String personident;
    private String miljoe;
    private List<DagpengesoknadDTO> nyeMottaDagpengesoknad;
    private List<DagpengevedtakDTO> nyeMottaDagpengevedtak;
    private List<DagpengevedtakDTO> nyeDagp;
}
