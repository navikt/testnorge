package no.nav.dolly.domain.resultSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TpsResponseStatus {

    private String kode;
    private String melding;
    private String utfyllendeMelding;

}
