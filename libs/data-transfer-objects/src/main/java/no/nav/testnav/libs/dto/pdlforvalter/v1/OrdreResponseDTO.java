package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdreResponseDTO {

    private PersonHendelserDTO hovedperson;
    private List<PersonHendelserDTO> relasjoner;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonHendelserDTO {

        private String ident;
        private List<PdlStatusDTO> ordrer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlStatusDTO {

        private PdlArtifact infoElement;
        private List<HendelseDTO> hendelser;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class HendelseDTO {

        private Integer id;
        private PdlStatus status;
        private String hendelseId;
        private String error;
        private Map<String, String> deletedOpplysninger;
    }
}
