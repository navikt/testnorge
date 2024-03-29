package no.nav.dolly.bestilling.aareg.amelding.domain;

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
public class AmeldingTransaksjon {

    private String id;
    private String maaned;
}
