package no.nav.dolly.domain.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GruppeBestillingIdent {

    private String ident;
    private Long id;
    private String bestkriterier;
    private String miljoer;
}