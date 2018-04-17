package no.nav.utilities;

import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import org.springframework.util.Assert;

public class TeamUtility {
	
	public static Bruker findBrukerOrEierInTeam(String opprettetAvNavIdent, Team teamtilhoerighet) {
		Assert.notNull(opprettetAvNavIdent, "Opprettet NavIdent kan ikke være null.");
		Bruker bruker = opprettetAvNavIdent.equals(teamtilhoerighet.getEier().getNavIdent())? teamtilhoerighet.getEier():null;
		if(bruker==null) {
			bruker=	teamtilhoerighet.getBrukere()
					.stream()
					.filter(teammedlem -> teammedlem.equals(opprettetAvNavIdent))
					.findFirst().get();
		}
		return bruker;
	}
}
