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
@Table("USER_ENTITY")
public class UserEntity implements Persistable<String> {

    @Transient
    private boolean isNew;

    @Id
    @Column("ID")
    private String id;

    @Column("USERNAME")
    private String brukernavn;

    @Column("EMAIL")
    private String epost;

    @Column("ORGANISASJONSNUMMER")
    private String organisasjonsnummer;

    @CreatedDate
    @Column("CREATED_AT")
    private LocalDateTime opprettet;

    @Column("LAST_LOGGED_IN")
    private LocalDateTime sistInnlogget;

    @Column("UPDATED_AT")
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



