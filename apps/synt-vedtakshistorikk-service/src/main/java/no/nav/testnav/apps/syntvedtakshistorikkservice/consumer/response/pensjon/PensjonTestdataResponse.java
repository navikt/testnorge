package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PensjonTestdataResponse {

    private List<PensjonTestdataStatus> status;
}
