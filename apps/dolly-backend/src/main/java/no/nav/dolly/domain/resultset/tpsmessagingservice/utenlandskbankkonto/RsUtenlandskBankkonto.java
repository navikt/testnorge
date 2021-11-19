package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;

public record RsUtenlandskBankkonto(String giroNrUtland,
                                    String kodeSwift,
                                    String kodeLand,
                                    String bankNavn,
                                    String valuta,
                                    String bankAdresse1,
                                    String bankAdresse2,
                                    String bankAdresse3) {
}
