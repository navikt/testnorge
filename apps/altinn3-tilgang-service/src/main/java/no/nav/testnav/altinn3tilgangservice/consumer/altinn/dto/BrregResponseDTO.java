package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrregResponseDTO {

    private Embedded _embedded;
    private Link _links;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Embedded {

        private List<Enhet> enheter;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Enhet {

        private String organisasjonsnummer;
        private String navn;
        private Organisasjonsform organisasjonsform;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Organisasjonsform {

        private String kode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Link {

        private Self self;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Self {

        private String href;
    }
}