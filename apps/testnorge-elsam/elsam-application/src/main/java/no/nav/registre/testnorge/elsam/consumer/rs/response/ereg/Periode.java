package no.nav.registre.testnorge.elsam.consumer.rs.response.ereg;

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
public class Periode {

    private String fom;
    private String tom;
}
