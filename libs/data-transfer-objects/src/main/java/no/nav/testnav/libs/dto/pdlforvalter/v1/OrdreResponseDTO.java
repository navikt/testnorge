package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdreResponseDTO {

    private PersonHendelserDTO hovedperson;
    private List<PersonHendelserDTO> relasjoner;

    public List<PersonHendelserDTO> getRelasjoner() {

        if (isNull(relasjoner)) {
            relasjoner = new ArrayList<>();
        }
        return relasjoner;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonHendelserDTO {

        private String ident;
        private List<PdlStatusDTO> ordrer;

        public List<PdlStatusDTO> getOrdrer() {

            if (isNull(ordrer)) {
                ordrer = new ArrayList<>();
            }
                return ordrer;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PdlStatusDTO {

        private String ident;
        private PdlArtifact infoElement;
        private List<HendelseDTO> hendelser;

        public List<HendelseDTO> getHendelser() {

            if (isNull(hendelser)) {
                hendelser = new ArrayList<>();
            }
            return hendelser;
        }

        @JsonIgnore
        public boolean isDataElement(){

            return infoElement != PdlArtifact.PDL_SLETTING &&
                    infoElement != PdlArtifact.PDL_SLETTING_HENDELSEID &&
                    infoElement != PdlArtifact.PDL_OPPRETT_PERSON &&
                    infoElement != PdlArtifact.PDL_PERSON_MERGE;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HendelseDTO {

        private Integer id;
        private PdlStatus status;
        private String hendelseId;
        private String error;
        private Map<String, String> deletedOpplysninger;
    }
}
