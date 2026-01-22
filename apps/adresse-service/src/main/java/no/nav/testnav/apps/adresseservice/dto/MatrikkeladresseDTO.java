package no.nav.testnav.apps.adresseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatrikkeladresseDTO {

    private AdresseDTO matrikkeladresse;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdresseDTO {

        private String id;
        private MatrikkelDTO matrikkelenhet;
        private Integer nummer;
        private String bokstav;
        private PostdataDTO postnummeromraade;
        private String adressetilleggsnavn;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatrikkelDTO {

        private String id;
        private MatrikkelnummerDTO matrikkelnummer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatrikkelnummerDTO {

        private String kommunenummer;
        private String gardsnummer;
        private String bruksnummer;
        private String festenummer;
        private String seksjonsnummer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostdataDTO {

        private String id;
        private String poststed;
        private String postnummer;
    }
}
