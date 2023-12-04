package no.nav.dolly.domain.resultset.tpsmessagingservice;

import no.nav.testnav.libs.data.kontoregister.v1.BankkontonrNorskDTO;

import java.util.List;

public record NorskBankkontoRequest(String ident,
                                    List<String> miljoer,
                                    BankkontonrNorskDTO body
) {
}
