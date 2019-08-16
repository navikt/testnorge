package no.nav.dolly.domain.resultset.udistub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Avgjoerelse {

	private String omgjortAvgjoerelsesId;
	private String utfallstypeKode;
	private String grunntypeKode;
	private String tillatelseKode;

	private boolean erPositiv;

	String utfallVarighetKode;
	Integer utfallVarighet;
	Periode utfallPeriode;
	String tillatelseVarighetKode;
	Integer tillatelseVarighet;
	Periode tillatelsePeriode;

	private Date effektueringsDato;
	private Date avgjoerelsesDato;
	private Date iverksettelseDato;
	private Date utreisefristDato;
	private Long saksnummer;
	private String etat;

	private boolean flyktningstatus;
	private boolean uavklartFlyktningstatus;
    private Person person;
}
