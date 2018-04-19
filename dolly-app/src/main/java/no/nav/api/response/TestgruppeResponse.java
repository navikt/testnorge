package no.nav.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestgruppeResponse {
	private Long id;
	private String navn;
	private String opprettetAvNavIdent;
	private String  sistEndretAvNavIdent;
	private LocalDateTime datoEndret;
	private String tilhoererTeamnavn;
	private Set<Long> testidenterID;
	
}
