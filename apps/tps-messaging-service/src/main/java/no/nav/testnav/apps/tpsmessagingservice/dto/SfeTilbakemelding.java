package no.nav.testnav.apps.tpsmessagingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.EndringsmeldingResponseDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SfeTilbakemelding {

    private EndringsmeldingResponseDTO svarStatus;
}
