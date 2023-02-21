package no.nav.testnav.personfastedataservice.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

import no.nav.testnav.libs.dto.personservice.v1.Gruppe;
import no.nav.testnav.personfastedataservice.domain.Person;
import no.nav.testnav.personfastedataservice.repository.converter.PersonJsonConverter;

@Entity
@Data
@Builder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PERSON")
public class PersonEntity {

    @Id
    @Column(name = "IDENT")
    private String ident;

    @Convert(converter = PersonJsonConverter.class)
    @Column(name = "DOKUMENT", nullable = false)
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(name = "GRUPPE", nullable = false)
    private Gruppe gruppe;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @CreatedDate
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT", nullable = false)
    @LastModifiedDate
    private Date updatedAt;

}
