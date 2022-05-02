package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.Value;

import java.util.List;

@Value
public class GraphQLResponse {

    private Data data;

    @Value
    public static class Data {

        private SokPerson sokPerson;
    }

    @Value
    public static class SokPerson {

        private Integer pageNumber;
        private Integer totalHits;
        private Integer totalPages;
        private List<Hits> hits;
    }

    @Value
    public static class Hits {

        private List<Identer> identer;
        private Person person;
    }

    @Value
    public static class Identer {

        private String ident;
        private String gruppe;
    }

    @Value
    public static class Person {

        private List<Navn> navn;
    }

    @Value
    public static class Navn {

        private String fornavn;
        private String etternavn;
        private String mellomnavn;
    }
}