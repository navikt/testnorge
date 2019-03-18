package no.nav.dolly.fasit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FasitResourceScope {

    private String environmentclass;
    private String zone;
    private String environment;
    private String application;
}
