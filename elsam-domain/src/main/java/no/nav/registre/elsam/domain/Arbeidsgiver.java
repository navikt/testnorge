package no.nav.registre.elsam.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Arbeidsgiver {

    private String navn;
    private String yrkesbetegnelse;
    private Double stillingsprosent;
}
