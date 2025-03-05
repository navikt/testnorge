package no.nav.testnav.libs.data.dollysearchservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonSearch {

    private Integer page;
    private Integer pageSize;
    private Integer terminateAfter;
    private String tag;
    private List<String> tags;
    private List<String> excludeTags;
    private String kjoenn;
    private String randomSeed;
    private Boolean kunLevende;
    private List<String> identer;
    private FoedselsdatoSearch foedselsdato;
    private SivilstandSearch sivilstand;
    private NasjonalitetSearch nasjonalitet;
    private AlderSearch alder;
    private IdentifikasjonSearch identifikasjon;
    private RelasjonSearch relasjoner;
    private PersonstatusSearch personstatus;
    private AdresserSearch adresser;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlderSearch {

        private Short fra;
        private Short til;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FoedselsdatoSearch {

        private LocalDate fom;
        private LocalDate tom;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IdentifikasjonSearch {

        private String identtype;
        private String adressebeskyttelse;
        private Boolean falskIdentitet;
        private Boolean utenlandskIdentitet;
        private Boolean identHistorikk;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NasjonalitetSearch {

        private String statsborgerskap;
        private InnflyttingSearch innflytting;
        private UtflyttingSearch utflytting;

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class InnflyttingSearch {

            private String fraflyttingsland;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class UtflyttingSearch {

            private String tilflyttingsland;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PersonstatusSearch {

        private String status;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdresserSearch {

        private BostedsadresseSearch bostedsadresse;
        private OppholdsadresseSearch oppholdsadresse;
        private String harUtenlandskAdresse;
        private String harKontaktadresse;
        private String harOppholdsadresse;

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class BostedsadresseSearch {

            private String borINorge;
            private String kommunenummer;
            private String bydelsnummer;
            private String postnummer;
            private String historiskKommunenummer;
            private String historiskBydelsnummer;
            private String historiskPostnummer;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class OppholdsadresseSearch {

            private String oppholdAnnetSted;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelasjonSearch {

        private String harBarn;
        private String harDoedfoedtBarn;
        private List<String> forelderBarnRelasjoner;
        private String foreldreansvar;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SivilstandSearch {

        private String type;
        private String tidligereType;
        private Boolean manglerSivilstand;
    }
}