package no.nav.testnav.apps.tpsmessagingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.EndringsmeldingResponseDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SfeTilbakemelding {

    private EndringsmeldingResponseDTO svarStatus;
}
