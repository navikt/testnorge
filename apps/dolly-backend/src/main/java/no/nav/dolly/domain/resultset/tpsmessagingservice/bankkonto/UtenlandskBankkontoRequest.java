package no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto;

import java.util.List;

public record UtenlandskBankkontoRequest(String ident,
                                         List<String> miljoer,
                                         RsUtenlandskBankkonto body
) {
}
