package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.pdl.forvalter.database.model.RelasjonType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullPersonDTO {

    private Long id;
    private PersonDTO person;
    private List<RelasjonDTO> relasjoner;
    private LocalDateTime sistOppdatert;

    public List<RelasjonDTO> getRelasjoner() {
        if (isNull(relasjoner)) {
            relasjoner = new ArrayList<>();
        }
        return relasjoner;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelasjonDTO {

        private Long id;

        private LocalDateTime sistOppdatert;
        private RelasjonType relasjonType;
        private PersonDTO relatertPerson;
    }
}