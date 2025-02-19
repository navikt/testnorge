package no.nav.dolly.opensearch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.data.dollysearchservice.v1.PersonRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    @Schema(description = "Paginering for bestillinger")
    private PagineringBestillingRequest pagineringBestillingRequest;

    @Schema(description = "Paginering for personersøk")
    private PagineringPersonRequest pagineringPersonRequest;

    @Schema(description = "Persondetaljer")
    private PersonRequest personRequest;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagineringBestillingRequest {

        @Schema(description = "Seed for paginering")
        private Integer seed;
        @Schema(description = "Sidenummer")
        private Integer side;
        @Schema(description = "Antall resultater per side")
        private Integer antall;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagineringPersonRequest {

        @Schema(description = "Sidenummer")
        private Integer side;
        @Schema(description = "Antall resultater per side")
        private Integer antall;
        @Schema(description = "Seed for paginering")
        private Integer seed;
    }
}
