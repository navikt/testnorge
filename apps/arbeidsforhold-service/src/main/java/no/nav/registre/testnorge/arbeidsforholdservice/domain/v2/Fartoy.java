package no.nav.registre.testnorge.arbeidsforholdservice.domain.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fartoy {

    private String skipsregister;
    private String skipstype;
    private String fartsomraade;
}
