package no.nav.dolly.domain.resultset.entity.testgruppe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsLockTestgruppe {

    private Boolean erLaast;
    private String laastBeskrivelse;
}
