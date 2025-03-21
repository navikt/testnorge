package no.nav.dolly.bestilling.brregstub.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RolleoversiktTo {

    private AdresseTo adresse;
    private List<RolleTo> enheter;
    private String fnr;
    private LocalDate fodselsdato;
    private Integer hovedstatus;
    private NavnTo navn;
    private List<Integer> understatuser;
    private String error;

    public static Mono<RolleoversiktTo> of(WebClientError.Description description) {
        return Mono.just(RolleoversiktTo
                .builder()
                .error(description.getMessage())
                .build());
    }

    public List<Integer> getUnderstatuser() {
        if (isNull(understatuser)) {
            understatuser = new ArrayList<>();
        }
        return understatuser;
    }

    public List<RolleTo> getEnheter() {
        if (isNull(enheter)) {
            enheter = new ArrayList<>();
        }
        return enheter;
    }

    public enum Egenskap {DELTAGER, KOMPLEMENTAR, KONTAKTPERSON, SAMEIER, STYRE}

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RolleTo {

        @EqualsAndHashCode.Exclude
        private NavnTo foretaksNavn;

        @EqualsAndHashCode.Exclude
        private AdresseTo forretningsAdresse;

        private Integer orgNr;

        private List<RolleStatus> personRolle;

        @EqualsAndHashCode.Exclude
        private AdresseTo postAdresse;

        @EqualsAndHashCode.Exclude
        private LocalDate registreringsdato;

        private String rolle;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NavnTo {

        private String navn1;
        private String navn2;
        private String navn3;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AdresseTo {

        private String adresse1;
        private String adresse2;
        private String adresse3;
        private String kommunenr;
        private String landKode;
        private String postnr;
        private String poststed;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RolleStatus {

        private Egenskap egenskap;
        private Boolean fratraadt;
    }
}