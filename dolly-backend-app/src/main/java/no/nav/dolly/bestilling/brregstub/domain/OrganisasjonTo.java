package no.nav.dolly.bestilling.brregstub.domain;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganisasjonTo {

    private SamendringTo deltakere;
    private Integer hovedstatus;
    private SamendringTo komplementar;
    private SamendringTo kontaktperson;
    private Integer orgnr;
    private LocalDate registreringsdato;
    private SamendringTo sameier;
    private SamendringTo styre;
    private List<Integer> understatuser;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SamendringTo {

        private LocalDate registringsDato;
        private List<PersonOgRolleTo> roller;

        public List<PersonOgRolleTo> getRoller() {
            if (isNull(roller)) {
                roller = new ArrayList<>();
            }
            return roller;
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PersonOgRolleTo {

        private String adresse1;
        private String fodselsnr;
        private String fornavn;
        private Boolean fratraadt;
        private String postnr;
        private String poststed;
        private String rolle;
        private String rollebeskrivelse;
        private String slektsnavn;
    }
}
