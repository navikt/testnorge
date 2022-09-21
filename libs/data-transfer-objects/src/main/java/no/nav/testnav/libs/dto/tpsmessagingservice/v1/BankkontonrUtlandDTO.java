package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankkontonrUtlandDTO {

    @With
    private String kontonummer;
    private String swift;
    private String iban;
    private String banknavn;
    private String valuta;
    private String bankAdresse1;
    private String bankAdresse2;
    private String bankAdresse3;
    private String landkode;
    private Boolean tilfeldigKontonummer;

    @Schema(description = "Datofeltet benyttes til lesning kun")
    private LocalDateTime kontoRegdato;
}
