package no.nav.testnav.examples.reactiverestexample.repository;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("CAR_ENTITY")
public class CarEntity implements Persistable<String> {

    @Transient
    private boolean isNew;

    @Id
    @Column("REGNUMBER")
    private String regnumber;

    @Column("COLOR")
    private String color;

    @Column("BRAND")
    private String brand;

    @CreatedDate
    @Column("CREATED_AT")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("UPDATED_AT")
    private LocalDateTime updatedAt;

    @Override
    public String getId() {
        return regnumber;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}



