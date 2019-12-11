package no.nav.registre.arena.core.consumer.rs.responses.rettighet.UngUfoer;

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
public class UngUfoerForvalterResponse {

    private List<NyeAaUngUfoer> nyeAaungufor;
    private List<NyeAaUngUfoerFeil> nyeAaunguforFeilList;
}
