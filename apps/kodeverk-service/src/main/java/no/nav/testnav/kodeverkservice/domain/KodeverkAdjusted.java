package no.nav.testnav.kodeverkservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KodeverkAdjusted {

    private String name;
    private List<KodeAdjusted> koder;

    public List<KodeAdjusted> getKoder() {
        if (isNull(koder)) {
            koder = new ArrayList<>();
        }
        return koder;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KodeAdjusted {

        private String label;
        private String value;
        private LocalDate gyldigFra;
        private LocalDate gyldigTil;
    }
}
