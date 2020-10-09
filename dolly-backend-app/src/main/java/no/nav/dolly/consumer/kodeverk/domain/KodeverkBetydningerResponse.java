package no.nav.dolly.consumer.kodeverk.domain;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KodeverkBetydningerResponse {

    private Map<String, List<Betydning>> betydninger;

    public Map<String, List<Betydning>> getBetydninger() {
        if (isNull(betydninger)) {
            betydninger = new HashMap<>();
        }
        return this.betydninger;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Betydning {

        private LocalDate gyldigFra;
        private LocalDate gyldigTil;
        private Map<String, Beskrivelse> beskrivelser;

        public Map<String, Beskrivelse> getBeskrivelser() {
            if (isNull(beskrivelser)) {
                beskrivelser = new HashMap<>();
            }
            return beskrivelser;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Beskrivelse {

        private String term;
        private String tekst;
    }
}
