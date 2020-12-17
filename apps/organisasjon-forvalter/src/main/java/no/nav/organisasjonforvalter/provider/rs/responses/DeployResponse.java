package no.nav.organisasjonforvalter.provider.rs.responses;

import lombok.*;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;

import java.util.*;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeployResponse {

    public enum Status {OK, ERROR}

    private Map<String, List<EnvStatus>> orgStatus;

    public Map<String, List<EnvStatus>> getOrgStatus() {
        return isNull(orgStatus) ? (orgStatus = new HashMap<>()) : orgStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnvStatus {

        private String environment;
        private Status status;
        private String details;
    }
}
