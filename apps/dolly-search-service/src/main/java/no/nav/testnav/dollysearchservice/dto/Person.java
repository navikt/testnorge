package no.nav.testnav.dollysearchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private HentIdenter hentIdenter;
    private HentPerson hentPerson;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HentIdenter {
        private List<Identer> identer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Identer {
        private String gruppe;
        private String ident;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HentPerson {
        private List<Navn> navn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Navn {

        private String fornavn;
        private String mellomnavn;
        private String etternavn;
    }
}
