package no.nav.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
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
	
	@ManyToOne
	@JoinColumn(name = "TILHOERER_GRUPPE", nullable = false)
	private Testgruppe testgruppe;
}