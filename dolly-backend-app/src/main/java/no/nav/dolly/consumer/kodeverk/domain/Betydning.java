package no.nav.dolly.consumer.kodeverk.domain;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Betydning {

    private LocalDate gyldigFra;
    private LocalDate gyldigTil;
    private Map<String, Beskrivelse> beskrivelser;

    public Map<String, Beskrivelse> getBeskrivelser() {
        if (isNull(beskrivelser)) {
            beskrivelser = new HashMap();
        }
        return beskrivelser;
    }
}
