package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.pdl.forvalter.utils.PdlTestDataUrls;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlOrdreResponse {

    private PersonHendelser hovedperson;
    private List<PersonHendelser> relasjoner;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonHendelser {

        private String ident;
        private List<PdlStatus> ordrer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlStatus {

        private PdlTestDataUrls.PdlArtifact infoElement;
        private List<Hendelse> hendelser;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Hendelse {

        private Integer id;
        private PdlTestDataUrls.PdlStatus status;
        private String hendelseId;
        private String error;
    }
}
