package no.nav.registre.arena.domain.rettighet;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NyRettighetResponse {

    @JsonAlias({"nyeAaungufor", "nyeAatfor", "nyeFritak"})
    private List<NyRettighet> nyeRettigheter;

    @JsonAlias({"nyeAaunguforFeilList", "nyeAatforFeilList", "nyeFritakFeilList"})
    private List<NyRettighetFeil> feiledeRettigheter;
}
