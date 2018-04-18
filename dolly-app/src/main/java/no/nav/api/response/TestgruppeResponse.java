package no.nav.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testident;

import java.time.LocalDateTime;
import java.util.Set;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestgruppeResponse {
	private Long id;
	private String navn;
	private Bruker opprettetAv;
	private Bruker sistEndretAv;
	private LocalDateTime datoEndret;
	private Team teamtilhoerighet;
	private Set<Testident> testidenter;
	
}
