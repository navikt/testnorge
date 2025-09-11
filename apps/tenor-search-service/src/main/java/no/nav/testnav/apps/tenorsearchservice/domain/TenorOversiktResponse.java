package no.nav.testnav.apps.tenorsearchservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenorOversiktResponse {

    private HttpStatus status;
    private Data data;
    private String query;
    private String error;

    @SuppressWarnings("java:S115")
    public enum TenorRelasjon {
        Arbeidsforhold,
        BeregnetSkatt,
        BrregErFr,
        Freg,
        Inntekt,
        TestinnsendingSkattPerson,
        SamletReskontroinnsyn,
        Skattemelding,
        Skatteplikt,
        SpesifisertSummertSkattegrunnlag,
        SummertSkattegrunnlag,
        Tilleggsskatt,
        Tjenestepensjonsavtale
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rollegruppe {
        private Type type;
        private List<String> fritekst;
        private List<Rolle> roller;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private Integer treff;
        private Integer rader;
        private Integer offset;
        private Integer nesteSide;
        private Integer seed;
        private List<Person> personer;

        public List<Person> getPersoner() {

            if (isNull(personer)) {
                personer = new ArrayList<>();
            }
            return personer;
        }
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person {

        private String id;
        private String fornavn;
        private String etternavn;
        private List<TenorRelasjon> tenorRelasjoner;
        private Boolean iBruk;
    }

    public TenorOversiktResponse copy() {

        return TenorOversiktResponse.builder()
                .status(this.getStatus())
                .query(this.getQuery())
                .data(Data.builder()
                        .treff(this.data.getTreff())
                        .rader(this.data.getRader())
                        .offset(this.data.getOffset())
                        .nesteSide(this.data.getNesteSide())
                        .seed(this.data.getSeed())
                        .personer(this.data.getPersoner())
                        .build())
                .error(this.getError())
                .build();
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Type {
        private String kode;
        private String beskrivelse;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Virksomhet {
        private String organisasjonsnummer;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rolle {
        private Type type;
        private Person person;
        private Virksomhet virksomhet;
        private Object valgtAv;
        private String fratraadt;
        private List<String> fritekst;
        private int rekkefolge;
    }
}