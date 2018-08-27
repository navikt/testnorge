package no.nav.dolly.domain.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Table(name = "T_GRUPPE")
public class Testgruppe {
	
	@Id
	@GeneratedValue(generator = "gruppeIdGenerator")
	@GenericGenerator(name = "gruppeIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
			@Parameter(name = "sequence_name", value = "T_GRUPPE_SEQ"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "increment_size", value = "1")
	})
	private Long id;
	
	@Column(nullable = false)
	private String navn;

	@Column
	private String hensikt;
	
	@ManyToOne
	@JoinColumn(name = "OPPRETTET_AV", nullable = false)
	private Bruker opprettetAv;
	
	@ManyToOne
	@JoinColumn(name = "SIST_ENDRET_AV", nullable = false)
	private Bruker sistEndretAv;

	@Column(name = "DATO_ENDRET", nullable = false)
	private LocalDate datoEndret;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TILHOERER_TEAM", nullable = false)
	private Team teamtilhoerighet;
	
	@OneToMany(mappedBy = "testgruppe")
	@Column(unique = true)
	private Set<Testident> testidenter = new HashSet<>();

	@ManyToMany(mappedBy ="favoritter", fetch = FetchType.LAZY)
	private Set<Bruker> favorisertAv = new HashSet<>();


}

