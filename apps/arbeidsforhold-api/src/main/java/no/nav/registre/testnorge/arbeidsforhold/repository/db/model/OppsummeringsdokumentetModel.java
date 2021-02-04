package no.nav.registre.testnorge.arbeidsforhold.repository.db.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "OPPLYSNINGSPLIKTIG")
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class OppsummeringsdokumentetModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ORGNUMMER", nullable = false, updatable = false)
    private String orgnummer;

    @Column(name = "YEAR", nullable = false, updatable = false)
    private Integer year;

    @Column(name = "MONTH", nullable = false, updatable = false)
    private Integer month;

    @Column(name = "VERSION", nullable = false, updatable = false)
    private Long version;

    @Column(name = "DOCUMENT", nullable = false, updatable = false)
    private String document;

    @Column(name = "ENVIRONMENT", nullable = false, updatable = false)
    private String miljo;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Date createdAt;
}