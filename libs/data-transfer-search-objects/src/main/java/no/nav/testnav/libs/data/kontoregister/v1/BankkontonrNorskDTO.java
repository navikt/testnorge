package no.nav.testnav.libs.data.kontoregister.v1;

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
public class BankkontonrNorskDTO {

    @With
    private String kontonummer;

    @Schema(description = "Datofeltet benyttes til lesning kun")
    private LocalDateTime kontoRegdato;

    private Boolean tilfeldigKontonummer;
}
