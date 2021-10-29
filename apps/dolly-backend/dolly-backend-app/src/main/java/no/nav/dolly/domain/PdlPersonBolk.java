package no.nav.dolly.domain;

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
    public static class Person {

        private List<PdlPerson.Navn> navn;
        private List<PdlPerson.Foedsel> foedsel;
        private List<PdlPerson.ForelderBarnRelasjon> forelderBarnRelasjon;
        private List<PdlPerson.Sivilstand> sivilstand;
        private List<PdlPerson.Doedsfall> doedsfall;
        private List<PdlPerson.UtflyttingFraNorge> utflyttingFraNorge;
        private List<PdlPerson.PdlKjoenn> kjoenn;
        private List<PdlPerson.Folkeregisteridentifikator> folkeregisteridentifikator;

        public List<PdlPerson.Navn> getNavn() {
            if (isNull(navn)) {
                navn = new ArrayList<>();
            }
            return navn;
        }

        public List<PdlPerson.Foedsel> getFoedsel() {
            if (isNull(foedsel)) {
                foedsel = new ArrayList<>();
            }
            return foedsel;
        }

        public List<PdlPerson.ForelderBarnRelasjon> getForelderBarnRelasjon() {
            if (isNull(forelderBarnRelasjon)) {
                forelderBarnRelasjon = new ArrayList<>();
            }
            return forelderBarnRelasjon;
        }

        public List<PdlPerson.Sivilstand> getSivilstand() {
            if(isNull(sivilstand)) {
                sivilstand = new ArrayList<>();
            }
            return sivilstand;
        }

        public List<PdlPerson.Doedsfall> getDoedsfall() {
            if (isNull(doedsfall)) {
                doedsfall = new ArrayList<>();
            }
            return doedsfall;
        }

        public List<PdlPerson.UtflyttingFraNorge> getUtflyttingFraNorge() {
            if (isNull(utflyttingFraNorge)) {
                utflyttingFraNorge = new ArrayList<>();
            }
            return utflyttingFraNorge;
        }

        public List<PdlPerson.PdlKjoenn> getKjoenn() {
            if (isNull(kjoenn)) {
                kjoenn = new ArrayList<>();
            }
            return kjoenn;
        }

        public List<PdlPerson.Folkeregisteridentifikator> getFolkeregisteridentifikator() {
            if (isNull(folkeregisteridentifikator)) {
                folkeregisteridentifikator = new ArrayList<>();
            }
            return folkeregisteridentifikator;
        }
    }
}
