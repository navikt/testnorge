package no.nav.dolly.domain.resultset.etterlatte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtterlatteYtelse {

    private YtelseType ytelse;

    public enum YtelseType {
        BARNEPENSJON,
        OMSTILLINGSSTOENAD
    }
}
