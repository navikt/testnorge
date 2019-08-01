package no.nav.registre.inst.fasit;

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
public class FasitResourceScope {

    private String environmentclass;
    private String zone;
    private String environment;
    private String application;
}
