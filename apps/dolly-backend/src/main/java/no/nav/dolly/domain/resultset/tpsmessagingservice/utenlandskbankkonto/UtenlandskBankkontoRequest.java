package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;

import java.util.List;

public record UtenlandskBankkontoRequest(String ident,
                                         List<String> miljoer,
                                         RsUtenlandskBankkonto body
) {
}
