package no.nav.testnav.libs.dto.dollysearchservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdentdataDTO {

    private String ident;
    private NavnDTO navn;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NavnDTO {

        private String fornavn;
        private String mellomnavn;
        private String etternavn;
    }
}
