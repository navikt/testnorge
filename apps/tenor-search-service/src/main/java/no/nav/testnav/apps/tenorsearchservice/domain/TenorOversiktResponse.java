package no.nav.testnav.apps.tenorsearchservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

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
    public static class Data {

        private Integer treff;
        private Integer rader;
        private Integer offset;
        private Integer nesteSide;
        private Integer seed;

        @Builder.Default
        private List<Person> personer= new ArrayList<>();
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
        private Boolean iArenaSynt;
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
}