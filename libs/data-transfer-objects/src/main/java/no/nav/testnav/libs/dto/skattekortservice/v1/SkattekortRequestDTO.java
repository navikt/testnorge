package no.nav.testnav.libs.dto.skattekortservice.v1;

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
public class SkattekortRequestDTO {

    private List<Arbeidsgiver> arbeidsgiver;

    public List<Arbeidsgiver> getArbeidsgiver() {

        if (isNull(arbeidsgiver)) {
            arbeidsgiver = new ArrayList<>();
        }
        return arbeidsgiver;
    }
}
