package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BankkontonrNorskDTO {

    @With
    private String kontonummer;

    @Schema(description = "Datofeltet benyttes til lesning kun")
    private LocalDateTime kontoRegdato;

    private Boolean tilfeldigKontonummer;
}
