package no.nav.dolly.domain.resultset;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RsBrukerTeamAndGruppeIDs {
    private String navIdent;
    List<RsTeamMedIdOgNavn> teams;
    List<String> favoritter;
}
