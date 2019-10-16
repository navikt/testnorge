package no.nav.dolly.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.team.RsTeamUtvidet;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Builder
public class RsTeamUtvidetBuilder {
    private Long id;
    private String navn;
    private String beskrivelse;
    private LocalDate datoOpprettet;
    private String eierNavIdent;
    private List<RsBruker> medlemmer;
    private List<RsTestgruppe> grupper;

    public RsTeamUtvidet convertToRealRsTeam() {
        RsTeamUtvidet team = new RsTeamUtvidet();
        team.setId(this.id);
        team.setNavn(this.navn);
        team.setBeskrivelse(this.beskrivelse);
        team.setDatoOpprettet(this.datoOpprettet);
        team.setEierNavIdent(this.eierNavIdent);
        team.setGrupper(this.grupper);
        team.setMedlemmer(this.medlemmer);

        return team;
    }
}
