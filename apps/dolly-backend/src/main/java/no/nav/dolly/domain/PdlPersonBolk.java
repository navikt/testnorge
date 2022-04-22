package no.nav.dolly.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlPersonBolk {

    private Data data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private List<PersonBolk> hentPersonBolk;
        private List<GeografiskTilknytningBolk> hentGeografiskTilknytningBolk;
        private List<GeografiskTilknytningBolk> hentIdenterBolk;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonBolk {

        private String ident;
        private PdlPerson.Person person;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeografiskTilknytningBolk {

        private String ident;
        private GeografiskTilknytning geografiskTilknytning;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdenterBolk {

        private String ident;
        private Identinformasjon identer;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeografiskTilknytning {

        private String gtType;
        private String gtLand;
        private String gtKommune;
        private String gtBydel;
        private String regel;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Identinformasjon {

        private String ident;
        private String gruppe;
    }
}
