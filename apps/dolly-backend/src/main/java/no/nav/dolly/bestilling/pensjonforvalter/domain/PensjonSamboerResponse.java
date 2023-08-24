package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PensjonSamboerResponse extends PensjonforvalterResponse {

    private List<Samboerforhold> samboerforhold;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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

            private Href self;
            private Href avslutt;
            private Href annuller;
            private Href endre;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Href {

            private String href;
            private Boolean templated;
        }
    }
}
