package no.nav.registre.testnorge.helsepersonellservice.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

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
        private List<IdenterBolk> hentIdenterBolk;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonBolk {

        private String ident;
        private Person person;
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
    public static class Identinformasjon {

        private String ident;
        private String gruppe;
        private Boolean historisk;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person {

        private List<Navn> navn;

        public List<Navn> getNavn() {
            if (isNull(navn)) {
                navn = new ArrayList<>();
            }
            return navn;
        }
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Navn {

        private String fornavn;
        private String mellomnavn;
        private String etternavn;
        private Metadata metadata;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {

        private boolean historisk;

        public boolean getHistorisk(){
            return historisk;
        }
    }
}
