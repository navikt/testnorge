package no.nav.dolly.bestilling.sykemelding.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding.RsDetaljertSykemelding;
import no.nav.dolly.domain.resultset.tpsf.Person;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestillingPersonWrapper {

    private Person person;
    private RsDetaljertSykemelding sykemelding;
}
