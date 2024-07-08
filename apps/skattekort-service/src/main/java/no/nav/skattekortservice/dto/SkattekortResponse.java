package no.nav.skattekortservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.skattekortservice.v1.Arbeidsgiver;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkattekortResponse {

    private SkattekortTilArbeidsgiver skattekortTilArbeidsgiver;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkattekortTilArbeidsgiver {

        private List<Arbeidsgiver> arbeidsgiver;

        public List<Arbeidsgiver> getArbeidsgiver() {

            if (isNull(arbeidsgiver)) {
                arbeidsgiver = new ArrayList<>();
            }
            return arbeidsgiver;
        }
    }
}
