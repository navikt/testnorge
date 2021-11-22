package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;

import java.time.LocalDate;

public record RsUtenlandskBankkonto(String kontonummerUtland, String kodeSwift, String kodeLand,
                                    LocalDate kontoRegdato, String iban,
                                    String bankNavn, String valuta, String bankAdresse1,
                                    String bankAdresse2, String bankAdresse3) {
}
