package no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto;

import java.util.List;

public record NorskBankkontoRequest(String ident,
                                    List<String> miljoer,
                                    RsNorskBankkonto body
) {
}
