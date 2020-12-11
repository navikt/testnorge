package no.nav.organisasjonforvalter.provider.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeployResponse {

    private List<OrgStatus> orgStatus;

    public List<OrgStatus> getOrgStatus() {
        if (isNull(orgStatus)) {
            orgStatus = new ArrayList<>();
        }
        return orgStatus;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrgStatus {

        private String orgnummer;
        private List<EnvStatus> envStatus;

        public List<EnvStatus> getEnvStatus() {
            if (isNull(envStatus)) {
                envStatus = new ArrayList<>();
            }
            return envStatus;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnvStatus {

        private String environment;
        private String status;
    }
}
