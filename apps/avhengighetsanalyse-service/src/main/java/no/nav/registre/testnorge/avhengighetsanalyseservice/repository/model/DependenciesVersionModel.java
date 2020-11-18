package no.nav.registre.testnorge.avhengighetsanalyseservice.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DEPENDENCIES_VERSION")
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class DependenciesVersionModel {

    @Id
    @Column(name = "HASH")
    private Long hash;

    @Column(name = "JSON", nullable = false, updatable = false)
    private String json;
}
