package no.nav.dolly.bestilling.organisasjonforvalter.domain;

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

    public enum Status {OK, ERROR}

    private Map<String, List<EnvStatus>> orgStatus;

    public Map<String, List<EnvStatus>> getOrgStatus() {
        return isNull(orgStatus) ? new HashMap<>() : orgStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnvStatus {

        private String environment;
        private String uuid;
        private Status status;
        private String details;
    }
}
