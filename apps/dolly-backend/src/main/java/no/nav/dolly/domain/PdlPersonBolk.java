package no.nav.dolly.domain;

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
public class PdlPersonBolk {

    private Data data;
    private Extensions extensions;

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
    public static class Extensions {

        private List<Warning> warnings;

        public List<Warning> getWarnings() {

            if (isNull(warnings)) {
                warnings = new ArrayList<>();
            }
            return warnings;
        }
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Warning {

        private String code;
        private String details;
        private String id;
        private String message;
        private String query;
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
