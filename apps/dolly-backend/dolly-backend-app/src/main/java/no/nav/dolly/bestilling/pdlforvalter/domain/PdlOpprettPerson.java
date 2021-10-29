package no.nav.dolly.bestilling.pdlforvalter.domain;

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
public class PdlOpprettPerson {

    private String opprettetIdent;
    private List<String> historiskeIdenter;

    public List<String> getHistoriskeIdenter() {
        if (isNull(historiskeIdenter)) {
            return new ArrayList<>();
        }
        return historiskeIdenter;
    }
}
