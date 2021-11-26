package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;

import java.util.List;

public record NorskBankkontoRequest(String ident,
                                    List<String> miljoer,
                                    RsNorskBankkonto body
) {
}
