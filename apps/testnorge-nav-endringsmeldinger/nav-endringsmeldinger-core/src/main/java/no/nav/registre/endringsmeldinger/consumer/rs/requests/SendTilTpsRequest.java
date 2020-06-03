package no.nav.registre.endringsmeldinger.consumer.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendTilTpsRequest {

    @NotNull
    private String miljoe;

    @NotNull
    private String melding;

    @NotNull
    private String ko;

    private Long timeout;
}
