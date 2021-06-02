package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.pdl.forvalter.database.model.RelasjonType;
import no.nav.pdl.forvalter.domain.PdlPerson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsPerson {

    private Long id;
    private PdlPerson person;
    private List<RsRelasjon> relasjoner;
    private LocalDateTime sistOppdatert;

    public List<RsRelasjon> getRelasjoner() {
        if (isNull(relasjoner)) {
            relasjoner = new ArrayList<>();
        }
        return relasjoner;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsRelasjon {

        private Long id;

        private LocalDateTime sistOppdatert;
        private RelasjonType relasjonType;
        private PdlPerson relatertPerson;
    }
}