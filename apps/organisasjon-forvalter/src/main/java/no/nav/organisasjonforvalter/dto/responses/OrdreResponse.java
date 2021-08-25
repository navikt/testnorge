package no.nav.organisasjonforvalter.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdreResponse {

    private Map<String, EnvStatus> orgStatus;

    public Map<String, EnvStatus> getOrgStatus() {
        if (isNull(orgStatus)) {
            orgStatus = new HashMap<>();
        }
        return orgStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EnvStatus {

        private String status;
        private String details;
    }
}
