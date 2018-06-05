package no.nav.jpa;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "T_TEST_IDENT")
public class Testident {
	
	@Id
	private Long ident;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TILHOERER_GRUPPE", nullable = false)
	private Testgruppe testgruppe;
}