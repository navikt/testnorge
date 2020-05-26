package no.nav.registre.testnorge.elsam.consumer.rs.response.aareg;

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
public class Arbeidsgiver {

    private String aktoertype;
    private String orgnummer;
}
