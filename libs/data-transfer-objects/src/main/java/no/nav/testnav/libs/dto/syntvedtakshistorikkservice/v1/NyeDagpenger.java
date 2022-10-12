package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NyeDagpenger {
    @JsonProperty
    private String fodselsnr;

    @JsonProperty
    private String miljoe;

    @JsonProperty
    private String personident;

    @JsonProperty
    private Integer oppgaveId;

    @JsonProperty
    private Integer arenaSakId;

    @JsonProperty
    private Integer vedtakId;

    @JsonProperty
    private String vedtakstatusKode;

    @JsonProperty
    private String avbruddBeskrivelse;

    @JsonProperty
    private String utfall;

    @JsonProperty
    private String begrunnelse;
}
