package no.nav.testnav.personfastedataservice.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
