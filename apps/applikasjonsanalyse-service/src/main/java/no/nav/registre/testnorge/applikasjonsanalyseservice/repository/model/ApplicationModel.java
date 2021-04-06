package no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "APPLICATION")
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationModel {

    @Id
    @Column(name = "SHA")
    private String sha;

    @Column(name = "NAME", nullable = false, updatable = false)
    private String name;

    @Column(name = "CONTENT", nullable = false, updatable = false)
    private String content;

    @Column(name = "ORGANISATION", nullable = false, updatable = false)
    private String organisation;

    @Column(name = "REPOSITORY", nullable = false, updatable = false)
    private String repository;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Date createdAt;

}
