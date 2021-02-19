package no.nav.registre.testnorge.arena.consumer.rs.response.pensjon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.arena.consumer.rs.response.pensjon.PensjonTestdataResponseDetails;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PensjonTestdataStatus {

    private String miljo;
    private PensjonTestdataResponseDetails response;
}
