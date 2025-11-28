package no.nav.testnav.libs.dto.kontoregister.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KontoDTO {
    private String kontohaver;
    private String kontonummer;

    private UtenlandskKontoDTO utenlandskKontoInfo;

    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;
    private String endretAv;
    private String opprettetAv;
}
