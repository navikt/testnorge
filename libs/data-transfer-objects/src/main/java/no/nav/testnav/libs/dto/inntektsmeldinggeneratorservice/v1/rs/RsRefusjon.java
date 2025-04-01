package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class RsRefusjon {

    private Double refusjonsbeloepPrMnd;
    private String refusjonsopphoersdato;
    private List<RsEndringIRefusjon> endringIRefusjonListe;

    public List<RsEndringIRefusjon> getEndringIRefusjonListe() {
        return Objects.requireNonNullElse(endringIRefusjonListe, Collections.emptyList());
    }
}
