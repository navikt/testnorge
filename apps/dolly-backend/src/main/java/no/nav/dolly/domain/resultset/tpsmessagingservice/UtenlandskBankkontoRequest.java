package no.nav.dolly.domain.resultset.tpsmessagingservice;

import no.nav.testnav.libs.dto.kontoregisterservice.v1.BankkontonrUtlandDTO;

import java.util.List;

public record UtenlandskBankkontoRequest(String ident,
                                         List<String> miljoer,
                                         BankkontonrUtlandDTO body
) {
}
