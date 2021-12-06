package no.nav.dolly.domain.resultset.pdldata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlPersondata {

    private PdlPerson opprettNyPerson;
    private PersonDTO person;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlPerson {

        private Identtype identtype;

        private LocalDateTime foedtEtter;
        private LocalDateTime foedtFoer;
        private Integer alder;
        private Boolean syntetisk;


        public enum Identtype {FNR, DNR, BOST;}
    }

    @JsonIgnore
    public boolean isTpsdataPresent() {

        return nonNull(person) &&
                (!person.getBostedsadresse().isEmpty() ||
                        !person.getKontaktadresse().isEmpty() ||
                        !person.getOppholdsadresse().isEmpty() ||
                        !person.getAdressebeskyttelse().isEmpty() ||
                        !person.getInnflytting().isEmpty() ||
                        !person.getUtflytting().isEmpty() ||
                        !person.getStatsborgerskap().isEmpty() ||
                        !person.getDoedsfall().isEmpty());
    }

    @JsonIgnore
    public boolean isPdlAdresse() {

        return nonNull(person) &&
                (!person.getBostedsadresse().isEmpty() ||
                        !person.getKontaktadresse().isEmpty() ||
                        !person.getOppholdsadresse().isEmpty());
    }
}
