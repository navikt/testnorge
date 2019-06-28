package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class RsTeamMedMedlemmerUtenGrupper {
    private Long id;
    private String navn;
    private String beskrivelse;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate datoOpprettet;

    private String eierNavIdent;
    private Set<RsBruker> medlemmer = new HashSet<>();

}
