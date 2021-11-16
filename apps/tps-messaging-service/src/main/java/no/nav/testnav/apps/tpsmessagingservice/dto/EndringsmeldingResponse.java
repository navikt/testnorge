package no.nav.testnav.apps.tpsmessagingservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class EndringsmeldingResponse {

    private SfeTilbakeMelding sfeTilbakeMelding;
}
