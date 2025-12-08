package no.nav.dolly.domain.resultset.kontoregister;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrUtlandDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankkontoData {
    private BankkontonrUtlandDTO utenlandskBankkonto;
    private BankkontonrNorskDTO norskBankkonto;
}
