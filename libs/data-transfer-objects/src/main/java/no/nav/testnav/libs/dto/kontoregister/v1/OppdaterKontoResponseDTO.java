package no.nav.testnav.libs.dto.kontoregister.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OppdaterKontoResponseDTO {
    private String kontohaver;
    private String kontonummer;
    private String opprettetAv;

    private UtenlandskKontoDTO utenlandskKonto;
}
