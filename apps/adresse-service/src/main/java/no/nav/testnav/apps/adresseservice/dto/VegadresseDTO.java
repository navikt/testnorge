package no.nav.testnav.apps.adresseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VegadresseDTO {

    private AdresseDTO vegadresse;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdresseDTO {

        private String id;
        private VegDTO veg;
        private Integer nummer;
        private String bokstav;
        private PostdataDTO postnummeromraade;
        private BydelDTO bydel;
        private String adressetilleggsnavn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VegDTO {

        private String id;
        private KommuneDTO kommune;
        private String adressekode;
        private String adressenavn;
        private String kortAdressenavn;
        private String stedsnummer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KommuneDTO {

        private String id;
        private String kommunenummer;
        private String kommunenavn;
        private FylkeDTO fylke;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FylkeDTO {

        private String id;
        private String fylkesnummer;
        private String fylkesnavn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostdataDTO {

        private String id;
        private String poststed;
        private String postnummer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BydelDTO {

        private String id;
        private String bydelsnummer;
        private String bydelsnavn;
    }
}
