package no.nav.registre.endringsmeldinger.provider.rs.responses;

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
public class StatusFraFeiledeMeldingerTpsResponse {

    private List<String> offentligIdentMedUtfyllendeMelding;
}
