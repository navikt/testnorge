package no.nav.registre.aareg.fasit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FasitResourceScope {

    private String environmentclass;
    private String zone;
    private String environment;
    private String application;
}
