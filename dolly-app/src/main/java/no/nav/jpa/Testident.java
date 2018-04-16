package no.nav.jpa;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_TEST_IDENT")
public class Testident {
	
	@Id
	private Long ident;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TILHOERER_GRUPPE", nullable = false)
	private Testgruppe testgruppe;
}