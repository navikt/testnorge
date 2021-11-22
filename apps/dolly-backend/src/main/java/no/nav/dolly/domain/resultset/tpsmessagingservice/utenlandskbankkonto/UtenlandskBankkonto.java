package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public record UtenlandskBankkonto(String kontonummerUtland, String kodeSwift, String kodeLand,
                                  LocalDate kontoRegdato, String iban,
                                  String bankNavn, String valuta, String bankAdresse1,
                                  String bankAdresse2, String bankAdresse3) {
}
