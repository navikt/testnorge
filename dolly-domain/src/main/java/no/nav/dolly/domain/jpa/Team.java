package no.nav.dolly.domain.jpa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "T_TEAM")
public class Team {

	public Team(String navn, Bruker eier){
		this.navn = navn;
		this.eier = eier;
		this.datoOpprettet = LocalDate.now();
		this.medlemmer = new HashSet<>(Arrays.asList(eier));
	}

	@Id
	@GeneratedValue(generator = "teamIdGenerator")
	@GenericGenerator(name = "teamIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
			@Parameter(name = "sequence_name", value = "T_TEAM_SEQ"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "increment_size", value = "1")
	})
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String navn;
	
	private String beskrivelse;
	
	@Column(name = "DATO_OPPRETTET", nullable = false)
	private LocalDate datoOpprettet;
	
	@ManyToOne
	@JoinColumn(name = "EIER", nullable = false)
	private Bruker eier;
	
	@OneToMany(mappedBy = "teamtilhoerighet")
	private Set<Testgruppe> grupper = new HashSet<>();
	
	@ManyToMany
	@JoinTable(name = "T_TEAM_MEDLEMMER",
			joinColumns = @JoinColumn(name = "team_id"),
			inverseJoinColumns = @JoinColumn(name = "bruker_id"))
	private Set<Bruker> medlemmer = new HashSet<>();
	
}
