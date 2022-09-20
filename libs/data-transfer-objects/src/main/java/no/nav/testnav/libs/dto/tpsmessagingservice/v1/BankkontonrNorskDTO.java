package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankkontonrNorskDTO {

    private String kontonummer;

    @Schema(description = "Datofeltet benyttes til lesning kun")
    private LocalDateTime kontoRegdato;
}
