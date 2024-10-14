package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensjonPoppGenerertInntektRequest {

    private String fnr;
    private List<PoppGenerertInntektRequest> inntekter;
    private List<String> miljoer;

    public List<String> getMiljoer() {

        if (isNull(miljoer)) {
            miljoer = new ArrayList<>();
        }
        return miljoer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PoppGenerertInntektRequest {

        private Integer aar;
        private Integer inntekt;

    }
}
