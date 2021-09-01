package no.nav.registre.varslingerservice.repository.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
@Table(name = "VARSLING")
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class VarslingModel {

    @Id
    @Column(name = "VARSLING_ID")
    private String varslingId;

    @Column(name = "FOM")
    private Date fom;

    @Column(name = "TOM")
    private Date tom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @CreatedDate
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT", nullable = false)
    @LastModifiedDate
    private Date updatedAt;
}
