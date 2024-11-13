package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AltinnResponseDTO {

    public static final String ORGANISASJON_ID = "urn:altinn:organization:identifier-no";

    private List<AccessListMembershipDTO> data;

    public List<AccessListMembershipDTO> getData() {

        if (isNull(data)) {
            data = new ArrayList<>();
        }
        return data;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessListMembershipDTO {

        private String id;
        private LocalDateTime since;
        private JsonNode identifiers;
    }
}
