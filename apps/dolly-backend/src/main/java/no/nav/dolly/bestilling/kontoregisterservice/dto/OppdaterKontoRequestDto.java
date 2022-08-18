package no.nav.dolly.bestilling.kontoregisterservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OppdaterKontoRequestDto {
    private String kontohaver;
    private String kontonummer;
    private String opprettetAv;

    private UtenlandskKontoDto utenlandskKonto;
}
