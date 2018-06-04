package no.nav.jpa;


import static no.nav.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "T_TEAM")
public class Team {
	
	@Id
	@GeneratedValue(generator = "teamIdGenerator")
	@GenericGenerator(name = "teamIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
			@Parameter(name = "sequence_name", value = "T_TEAM_SEQ"),
			@Parameter(name = "initial_value", value = "100000000"),
			@Parameter(name = "increment_size", value = "1")
	})
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String navn;
	
	private String beskrivelse;
	
	@Column(name = "DATO_OPPRETTET", nullable = false)
	private LocalDateTime datoOpprettet;
	
	@ManyToOne
	@JoinColumn(name = "EIER",nullable = false)
	private Bruker eier;
	
	@OneToMany(mappedBy = "teamtilhoerighet", orphanRemoval = true)
	@Column(name = "GRUPPER", unique = true)
	private Set<Testgruppe> grupper;
	
	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinTable(name = "team_medlemmer",
			joinColumns = @JoinColumn(name = "team_id"),
			inverseJoinColumns = @JoinColumn(name = "bruker_id"))
	@Builder.Default
	private Set<Bruker> medlemmer = new HashSet<>();
	
	
	public Set<String> getSetOfBrukernesNavidenter() {
		Set<String> brukernesNavIdent = new HashSet<>();
		this.getMedlemmer().forEach(bruker -> brukernesNavIdent.add(bruker.getNavIdent()));
		return brukernesNavIdent;
	}
}
