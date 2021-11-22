package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public final class UtenlandskBankkonto {
    private final String kontonummer;
    private final String swift;
    private final String landkode;
    private final String iban;
    private final String banknavn;
    private final String valuta;
    private final String bankAdresse1;
    private final String bankAdresse2;
    private final String bankAdresse3;
}
