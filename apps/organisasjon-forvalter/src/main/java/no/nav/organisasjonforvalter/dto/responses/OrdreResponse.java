package no.nav.organisasjonforvalter.dto.responses;

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
public class OrdreResponse {

    private Map<String, List<EnvStatus>> orgStatus;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EnvStatus {

        private String environment;
        private String status;
        private String details;
    }
}
