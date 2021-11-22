package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class UtenlandskBankkonto {
    private String kontonummer;
    private String swift;
    private String landkode;
    private String iban;
    private String banknavn;
    private String valuta;
    private String bankAdresse1;
    private String bankAdresse2;
    private String bankAdresse3;
}
