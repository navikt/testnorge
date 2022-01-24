package no.nav.dolly.consumer.pdlperson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlBolkResponse {

    private Contents data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contents {
        private List<BolkPerson> hentPersonBolk;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BolkPerson {

        private String ident;
        private PersonDTO person;
    }
}
