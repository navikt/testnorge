package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public record UtenlandskBankkonto(String kontonummer, String swift, String landkode,
                                  LocalDateTime kontoRegdato, String iban,
                                  String banknavn, String valuta, String bankAdresse1,
                                  String bankAdresse2, String bankAdresse3) {
}
