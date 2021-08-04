package no.nav.testnav.libs.dto.dokarkiv.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DokmotResponse {
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
