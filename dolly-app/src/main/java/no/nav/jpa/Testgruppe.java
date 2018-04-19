package no.nav.jpa;

import static no.nav.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"teamtilhoerighet"}) //eksluderer cyclic references for å unngå stackoverflowError
@EqualsAndHashCode(exclude = {"teamtilhoerighet"})
@Table(name = "T_TESTGRUPPE")
public class Testgruppe {
	
	@Id
	@GeneratedValue(generator = "gruppeIdGenerator")
	@GenericGenerator(name = "gruppeIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
			@Parameter(name = "sequence_name", value = "T_GRUPPE_SEQ"),
			@Parameter(name = "initial_value", value = "100000000"),
			@Parameter(name = "increment_size", value = "1")
	})
	private Long id;
	
	@Column(nullable = false)
	private String navn;
	
	@ManyToOne
	@JoinColumn(name = "OPPRETTET_AV", nullable = false)
	private Bruker opprettetAv;
	
	@ManyToOne
	@JoinColumn(name = "SIST_ENDRET_AV", nullable = false)
	private Bruker sistEndretAv;
	
	@Column(name = "DATO_ENDRET", nullable = false)
	private LocalDateTime datoEndret;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TILHOERER_TEAM", nullable = false, referencedColumnName = "id")
	private Team teamtilhoerighet;
	
	@OneToMany(mappedBy = "testgruppe", orphanRemoval = true)
	@Column(unique = true)
	private Set<Testident> testidenter;
}

