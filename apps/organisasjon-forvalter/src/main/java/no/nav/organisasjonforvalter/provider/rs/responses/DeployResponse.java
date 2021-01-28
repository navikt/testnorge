package no.nav.organisasjonforvalter.provider.rs.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeployResponse {

    private Map<String, List<EnvStatus>> orgStatus;

    public Map<String, List<EnvStatus>> getOrgStatus() {
        if (isNull(orgStatus)) {
            orgStatus = new HashMap<>();
        }
        return orgStatus;
    }

    public enum Status {OK, ERROR}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EnvStatus {

        private String environment;
        private String uuid;
        private Status status;
        private String details;
    }
}
