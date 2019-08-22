package no.nav.dolly.domain.resultset.udistub.model.opphold;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvslagEllerBortFall {

	private Date avgjorelsesDato; 
	private Date bortfallAvPOellerBOSDato; 

	private Date tilbakeKallVirkningsDato; 
	private Date tilbakeKallUtreiseFrist; 

	private Date avslagOppholdstillatelseUtreiseFrist; 
	private String avslagGrunnlagOverig; 

	private String avslagGrunnlagTillatelseGrunnlagEOS; 
	private String avslagOppholdstillatelseBehandletGrunnlagEOS; 

	private Date avslagOppholdstillatelseBehandletUtreiseFrist; 
	private String avslagOppholdstillatelseBehandletGrunnlagOvrig; 
	private String avslagOppholdsrettBehandlet; 
	private Date formeltVedtakUtreiseFrist; 
}
