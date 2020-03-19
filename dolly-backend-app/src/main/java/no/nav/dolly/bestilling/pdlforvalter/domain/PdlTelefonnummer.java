package no.nav.dolly.bestilling.pdlforvalter.domain;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlTelefonnummer {

    private List<Entry> telfonnumre;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {
        private String kilde;
        private String landskode;
        private String nummer;
        private Integer prioritet;
    }

    public List<Entry> getTelfonnumre() {
        if (isNull(telfonnumre)) {
            telfonnumre = new ArrayList();
        }
        return telfonnumre;
    }
}
