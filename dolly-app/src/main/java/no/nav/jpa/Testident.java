package no.nav.jpa;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

import static no.nav.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_TEST_IDENT")
public class Testident {

    @Id
    private Long ident;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "GRUPPETILHOERIGHET", nullable = false)
    private Testgruppe testgruppe;
}