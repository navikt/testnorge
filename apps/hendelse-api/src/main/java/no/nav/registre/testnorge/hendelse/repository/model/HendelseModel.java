package no.nav.registre.testnorge.hendelse.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

import no.nav.registre.testnorge.dto.hendelse.v1.HendelseType;

@Entity
@Table(name = "hendelse")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class HendelseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "hendelse", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private HendelseType hendelse;

    @Temporal(TemporalType.DATE)
    @Column(name = "fom", nullable = false, updatable = false)
    private Date fom;

    @Temporal(TemporalType.DATE)
    @Column(name = "tom")
    private Date tom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "opprettet", nullable = false, updatable = false)
    @CreatedDate
    private Date opprettet;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ident_id", referencedColumnName = "id", nullable = false)
    private IdentModel ident;
}