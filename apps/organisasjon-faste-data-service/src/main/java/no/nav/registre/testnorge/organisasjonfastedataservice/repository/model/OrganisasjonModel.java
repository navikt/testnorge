package no.nav.registre.testnorge.organisasjonfastedataservice.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;
import no.nav.registre.testnorge.organisasjonfastedataservice.repository.converter.OrganisasjonJsonConverter;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "OVERENHET")
    @Builder.Default
    private List<OrganisasjonModel> underenheter = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @CreatedDate
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT", nullable = false)
    @LastModifiedDate
    private Date updatedAt;

}
