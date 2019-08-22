package no.nav.dolly.domain.resultset.udistub.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UtvistMedInnreiseForbud {

	private String innreiseForbud;

	private Date innreiseForbudVedtaksDato;
	private String varighet;
}
