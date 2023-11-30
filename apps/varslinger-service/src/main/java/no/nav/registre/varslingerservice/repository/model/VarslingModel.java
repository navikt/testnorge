package no.nav.registre.varslingerservice.repository.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "VARSLING")
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VarslingModel that = (VarslingModel) o;
        return getVarslingId() != null && Objects.equals(getVarslingId(), that.getVarslingId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
