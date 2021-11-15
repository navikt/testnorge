package no.nav.testnav.apps.tpsmessagingservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class EndringsmeldingResponse {

    private SfeTilbakemelding sfeTilbakemelding;
}
