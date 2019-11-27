package no.nav.dolly.consumer.pdlperson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {
    private String opplysningsId;
    private String master;
    private List<Endring> endringer;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Endring {
        private String type;
        private LocalDateTime registrert;
        private String registrertAv;
        private String systemkilde;
        private String kilde;
    }
}
