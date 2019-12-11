package no.nav.registre.arena.core.consumer.rs.responses.rettighet.UngUfoer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NyeAaUngUfoerFeil {

    private String personident;
    private String miljoe;
    private String nyAaunguforFeilstatus;
    private String melding;
}
