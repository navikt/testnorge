package no.nav.api.resultSet;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;

@Getter
@Setter
public class RsTestgruppe {
	private Long id;
	private String navn;
	private RsBruker opprettetAv;
	private RsBruker sistEndretAv;
	private LocalDateTime datoEndret;
	private HashSet<RsTestident> testidenter;
	
}
