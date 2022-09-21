package no.nav.dolly.domain.resultset.tpsmessagingservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankkontoData {
    private BankkontonrUtlandDTO utenlandskBankkonto;
    private BankkontonrNorskDTO norskBankkonto;
}
