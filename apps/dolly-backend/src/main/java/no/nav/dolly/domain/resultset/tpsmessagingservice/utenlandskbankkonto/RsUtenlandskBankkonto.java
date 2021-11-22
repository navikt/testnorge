package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;

public record RsUtenlandskBankkonto(String kontonummer, String swift,
                                    String landkode, String iban,
                                    String banknavn, String valuta, String bankAdresse1,
                                    String bankAdresse2, String bankAdresse3) {
}