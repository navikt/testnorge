package no.nav.registre.testnorge.organisasjonfastedataservice.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Gruppe;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;
import no.nav.registre.testnorge.organisasjonfastedataservice.repository.converter.OrganisasjonJsonConverter;

@Entity
@Data
@Builder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ORGANISASJON")
public class OrganisasjonModel {

    @Id
    @Column(name = "ORGNUMMER")
    private String orgnummer;

    @Convert(converter = OrganisasjonJsonConverter.class)
    @Column(name = "DOKUMENT", nullable = false)
    private Organisasjon organisasjon;

    @Column(name = "OVERENHET")
    private String overenhet;

    @Enumerated(EnumType.STRING)
    @Column(name = "GRUPPE", nullable = false)
    private Gruppe gruppe;

    @OneToMany(mappedBy = "overenhet", cascade = CascadeType.ALL)
    private List<OrganisasjonModel> underenheter;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @CreatedDate
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT", nullable = false)
    @LastModifiedDate
    private Date updatedAt;

}
