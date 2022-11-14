package no.nav.dolly.domain.resultset.arenaforvalter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArenaNyBruker {

    private String personident;
    private String miljoe;
    private LocalDate aktiveringsDato;
    private ArenaKvalifiseringsgruppe kvalifiseringsgruppe;
    private ArenaBrukerUtenServicebehov utenServicebehov;
    private Boolean automatiskInnsendingAvMeldekort;
    private List<ArenaAap115> aap115;
    private List<ArenaAap> aap;
}
