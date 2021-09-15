package no.nav.testnav.apps.apptilganganalyseservice.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;


@Table("APPLICATION_ENTITY")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationEntity implements Persistable<String> {

    @Id
    @Column("SHA")
    private String sha;

    @Column("CONTENT")
    private String content;

    @Column("REPO")
    private String repo;

    @CreatedDate
    @Column("CREATED_AT")
    private LocalDateTime createdAt;

    @Override
    public String getId() {
        return sha;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
