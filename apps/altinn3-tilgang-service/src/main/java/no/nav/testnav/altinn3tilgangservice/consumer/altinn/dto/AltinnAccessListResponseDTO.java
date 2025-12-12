package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

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
    private LinksDTO links;
    private String feilmelding;
    private HttpStatus status;

    public static Mono<AltinnAccessListResponseDTO> of(WebClientError.Description description) {
        return Mono.just(AltinnAccessListResponseDTO
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .build());
    }

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinksDTO {

        private String next;
    }
}
