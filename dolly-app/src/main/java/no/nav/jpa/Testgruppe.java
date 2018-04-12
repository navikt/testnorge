package no.nav.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

import static no.nav.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_TESTGRUPPE")
public class Testgruppe {

    @Id
    @GeneratedValue(generator = "gruppeIdGenerator")
    @GenericGenerator(name= "gruppeIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_GRUPPE_SEQ"),
            @Parameter(name = "initial_value", value = "100000000"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;
    
    @Column(nullable = false)
    private String navn;
    
    @ManyToOne
    @JoinColumn(name = "NAV_IDENT")
    @Column(name = "OPPRETTET_AV",nullable = false)
    private Testident opprettetAv;
    
    @ManyToOne
    @JoinColumn(name = "NAV_IDENT")
    @Column(name = "SIST_ENDRET_AV",nullable = false)
    private Testident sistEndretAv;
    
    @Column(name = "DATO_ENDRET", nullable = false)
    private  LocalDateTime datoEndret;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @Column(nullable = false)
    private Team teamtilhoerighet;

    @OneToMany(mappedBy = "testgruppe", orphanRemoval = true)
    @Column(unique = true)
    private Set<Testident> testidenter;
}
