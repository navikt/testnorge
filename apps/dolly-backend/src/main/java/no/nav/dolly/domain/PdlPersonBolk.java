package no.nav.dolly.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlPersonBolk {

    private Data data;
    private JsonNode extensions;

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private List<PersonBolk> hentPersonBolk;
        private List<GeografiskTilknytningBolk> hentGeografiskTilknytningBolk;
        private List<IdenterBolk> hentIdenterBolk;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonBolk {

        private String ident;
        private PdlPerson.Person person;
        private String code;
    }

    @lombok.Data
    @Builder
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
        private List<Identinformasjon> identer;
    }

    @lombok.Data
    @Builder
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
        private Boolean historisk;
    }
}
