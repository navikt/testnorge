package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlTelefonnummer {

    private List<Entry> telfonnumre;

    public List<Entry> getTelfonnumre() {
        if (isNull(telfonnumre)) {
            telfonnumre = new ArrayList<>();
        }
        return telfonnumre;
    }

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
}
