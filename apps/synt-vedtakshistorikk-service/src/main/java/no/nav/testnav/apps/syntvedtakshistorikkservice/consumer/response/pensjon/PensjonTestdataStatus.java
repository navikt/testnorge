package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PensjonTestdataStatus {

    private String miljo;
    private PensjonTestdataResponseDetails response;
}
