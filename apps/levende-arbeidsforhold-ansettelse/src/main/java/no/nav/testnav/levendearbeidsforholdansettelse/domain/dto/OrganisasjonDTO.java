package no.nav.testnav.levendearbeidsforholdansettelse.domain.dto;

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
public class OrganisasjonDTO {

    private Data data;

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private List<Organisasjon> organisasjoner;

        public List<Organisasjon> getOrganisasjoner() {

            if (isNull(organisasjoner)) {
                organisasjoner = new ArrayList<>();
            }
            return organisasjoner;
        }
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Organisasjon {

        private String organisasjonsnummer;
        private TenorRelasjoner tenorRelasjoner;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TenorRelasjoner {

        private List<BrregTrivia> brregErFr;

        public List<BrregTrivia> getBrregErFr() {

            if (isNull(brregErFr)) {
                brregErFr = new ArrayList<>();
            }
            return brregErFr;
        }
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrregTrivia {

        private Adresse forretningsadresse;
        private Adresse postadresse;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Adresse {

        private String postnummer;
    }
}
