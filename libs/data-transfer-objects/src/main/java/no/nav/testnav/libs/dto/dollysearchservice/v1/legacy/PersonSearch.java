package no.nav.testnav.libs.dto.dollysearchservice.v1.legacy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonSearch {

    private Integer page;
    private Integer pageSize;
    private String kjoenn;
    private Integer randomSeed;
    private Boolean kunLevende;
    private NasjonalitetSearch nasjonalitet;
    private AlderSearch alder;
    private RelasjonSearch relasjoner;
    private PersonstatusSearch personstatus;
    private AdresserSearch adresser;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlderSearch {

        private Integer fra;
        private Integer til;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NasjonalitetSearch {

        private String statsborgerskap;
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

        private Boolean harUtenlandskAdresse;
        private Boolean harKontaktadresse;
        private Boolean harOppholdsadresse;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelasjonSearch {

        private Boolean harBarn;
    }
}