package no.nav.registre.inntekt.domain.dokmot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoarkResponse {
    @JsonProperty
    private List<DokumentInfo> dokumenter;
    @JsonProperty
    private String journalpostId;
    @JsonProperty
    private String journalstatus;
    @JsonProperty
    private String melding;
    @JsonProperty
    private Boolean journalpostferdigstilt;
}
