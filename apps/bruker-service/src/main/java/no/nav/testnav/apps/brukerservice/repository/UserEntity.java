package no.nav.testnav.apps.brukerservice.repository;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("user_entity")
public class UserEntity implements Persistable<String> {

    @Transient
    private boolean isNew;

    @Id
    @Column("id")
    private String id;

    @Column("username")
    private String brukernavn;

    @Column("email")
    private String epost;

    @Column("organisasjonsnummer")
    private String organisasjonsnummer;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime opprettet;

    @Column("last_logged_in")
    private LocalDateTime sistInnlogget;

    @Column("updated_at")
    private LocalDateTime oppdatert;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}



