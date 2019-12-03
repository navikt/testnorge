package no.nav.dolly.domain.resultset.entity.bestilling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsMalBestillingWrapper {

    private String malNavn;
    private RsMalBestilling mal;
}