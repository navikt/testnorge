package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AltinnResponseDTO {

    private List<AccessListMembershipDTO> data;

    public List<AccessListMembershipDTO> getData() {

        if (isNull(data)) {
            data = new ArrayList<>();
        }
        return data;
    }

    @Data
    public static class AccessListMembershipDTO {

        private String id;
        private LocalDateTime since;
        private String identifiers;
    }
}
