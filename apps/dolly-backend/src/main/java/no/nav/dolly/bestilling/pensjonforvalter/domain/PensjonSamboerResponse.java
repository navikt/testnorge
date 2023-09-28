package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PensjonSamboerResponse extends PensjonforvalterResponse {

    private List<Samboerforhold> samboerforhold;

    public List<Samboerforhold> getSamboerforhold() {

        if (isNull(samboerforhold)) {
            samboerforhold = new ArrayList<>();
        }
        return samboerforhold;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @SuppressWarnings("java:S1700")
    public static class Samboerforhold {
        private String pidBruker;
        private String pidSamboer;
        private LocalDate datoFom;
        private LocalDate datoTom;
        private String registrertAv;
        private Lenker _links;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Lenker {

            private Lenke self;
            private Lenke avslutt;
            private Lenke annuller;
            private Lenke endre;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Lenke {

            private String href;
            private Boolean templated;
        }
    }
}
