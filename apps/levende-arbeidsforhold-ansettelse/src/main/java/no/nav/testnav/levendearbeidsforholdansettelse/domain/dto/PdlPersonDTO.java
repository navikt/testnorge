package no.nav.testnav.levendearbeidsforholdansettelse.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlPersonDTO {

    private JsonNode errors;

    private Data data;

    public enum Gruppe {AKTORID, FOLKEREGISTERIDENT, NPID}

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private SokPerson sokPerson;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SokPerson {

        private List<Hit> hits;

        public List<Hit> getHits() {

            if (isNull(hits)) {
                hits = new ArrayList<>();
            }
            return hits;
        }
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hit {

        private List<Ident> identer;
        private Person person;

        public List<Ident> getIdenter() {

            if (isNull(identer)) {
                identer = new ArrayList<>();
            }
            return identer;
        }
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ident {

        private String ident;
        private Gruppe gruppe;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person {

        private List<NavnDTO> navn;
        private List<FoedselsdatoDTO> foedselsdato;
        private List<Folkeregisteridentifikator> folkeregisteridentifikator;
        private List<BostedadresseDTO> bostedsadresse;
        private List<OppholdsadresseDTO> oppholdsadresse;

        public List<NavnDTO> getNavn() {

            if (isNull(navn)) {
                navn = new ArrayList<>();
            }
            return navn;
        }

        public List<FoedselsdatoDTO> getFoedselsdato() {

            if (isNull(foedselsdato)) {
                foedselsdato = new ArrayList<>();
            }
            return foedselsdato;
        }

        public List<Folkeregisteridentifikator> getFolkeregisteridentifikator() {

            if (isNull(folkeregisteridentifikator)) {
                folkeregisteridentifikator = new ArrayList<>();
            }
            return folkeregisteridentifikator;
        }

        public List<BostedadresseDTO> getBostedsadresse() {
            if (isNull(bostedsadresse)) {
                bostedsadresse = new ArrayList<>();
            }
            return bostedsadresse;
        }

        public List<OppholdsadresseDTO> getOppholdsadresse() {
            if (isNull(oppholdsadresse)) {
                oppholdsadresse = new ArrayList<>();
            }
            return oppholdsadresse;
        }

        @lombok.Data
        @EqualsAndHashCode(callSuper = true)
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Folkeregisteridentifikator extends DbVersjonDTO {

            private String identifikasjonsnummer;
            private String type;
            private String status;

            public boolean isIBRUK() {
                return "I_BRUK".equals(status);
            }
        }
    }
}