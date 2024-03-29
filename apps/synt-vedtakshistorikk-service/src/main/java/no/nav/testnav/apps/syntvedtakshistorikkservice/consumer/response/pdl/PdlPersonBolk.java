package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl;

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
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonBolk {

        private String ident;
        private PdlPerson.HentPerson person;
    }
}
