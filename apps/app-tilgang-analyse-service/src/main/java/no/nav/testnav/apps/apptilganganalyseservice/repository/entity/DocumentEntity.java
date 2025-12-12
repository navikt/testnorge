package no.nav.testnav.apps.apptilganganalyseservice.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.apps.apptilganganalyseservice.domain.DocumentType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;


@Table("document_entity")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentEntity implements Persistable<String> {

    @Id
    @Column("sha")
    private String sha;

    @Column("content")
    private String content;

    @Column("repo")
    private String repo;

    @Column("owner")
    private String owner;

    @Column("type")
    private DocumentType type;

    @Column("path")
    private String path;

    @CreatedDate
    @Column("created_at")
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
