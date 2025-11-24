package no.nav.dolly.domain.resultset.entity.bruker;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.Bruker.Brukertype;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsBruker {

    private String brukerId;
    private String brukernavn;
    private Brukertype brukertype;
    private String epost;
    private String navIdent;
    private RsTeamWithBrukere representererTeam;
    private List<String> grupper;
    private List<RsTestgruppe> favoritter;
}