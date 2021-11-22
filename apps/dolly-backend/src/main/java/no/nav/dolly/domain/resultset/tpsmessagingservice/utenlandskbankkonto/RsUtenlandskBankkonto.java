package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;

import java.time.LocalDateTime;

public record RsUtenlandskBankkonto(String kontonummer, String swift, String landkode,
                                    LocalDateTime kontoRegdato, String iban,
                                    String banknavn, String valuta, String bankAdresse1,
                                    String bankAdresse2, String bankAdresse3) {
}