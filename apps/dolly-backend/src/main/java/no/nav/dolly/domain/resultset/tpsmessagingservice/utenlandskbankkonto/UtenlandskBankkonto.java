package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
