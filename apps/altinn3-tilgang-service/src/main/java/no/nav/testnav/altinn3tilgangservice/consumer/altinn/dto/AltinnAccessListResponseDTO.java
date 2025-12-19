package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AltinnAccessListResponseDTO {

    private List<AccessListMembershipDTO> data;
    private String feilmelding;
    private HttpStatus status;

    public List<AccessListMembershipDTO> getData() {

        if (isNull(data)) {
            data = new ArrayList<>();
        }
        return data;
    }

    public static Mono<AltinnAccessListResponseDTO> of(WebClientError.Description description) {
        return Mono.just(AltinnAccessListResponseDTO
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .build());
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
