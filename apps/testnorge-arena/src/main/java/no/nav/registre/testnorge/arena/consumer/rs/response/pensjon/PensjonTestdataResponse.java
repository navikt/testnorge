package no.nav.registre.testnorge.arena.consumer.rs.response.pensjon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PensjonTestdataResponse {

    private List<PensjonTestdataStatus> status;
}
