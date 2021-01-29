package no.nav.organisasjonforvalter.provider.rs.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.Status.ERROR;
import static no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.Status.OK;

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

        @JsonIgnore
        public boolean isOk() {
            return status == OK;
        }

        @JsonIgnore
        public boolean isError() {
            return status == ERROR;
        }
    }
}
