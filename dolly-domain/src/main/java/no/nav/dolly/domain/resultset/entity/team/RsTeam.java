package no.nav.dolly.domain.resultset.entity.team;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class RsTeam {
    private Long id;
    private String navn;
    private String beskrivelse;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate datoOpprettet;

    private String eierNavIdent;
    private Integer antallMedlemmer;
}