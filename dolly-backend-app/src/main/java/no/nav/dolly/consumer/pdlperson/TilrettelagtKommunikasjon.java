package no.nav.dolly.consumer.pdlperson;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TilrettelagtKommunikasjon {
    private Tolk tolk;
    private List<Muntlig> muntlig;
    private boolean skriftlig;
    private Metadata metadata;

    public enum Muntlig {
        LESEVANSKER,
        SYNSHEMMET
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tolk {
        private String spraak;
        private boolean tegnspraak;
    }
}
