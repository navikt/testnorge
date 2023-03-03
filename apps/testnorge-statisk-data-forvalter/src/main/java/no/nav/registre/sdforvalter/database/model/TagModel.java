package no.nav.registre.sdforvalter.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tags")
@EqualsAndHashCode(callSuper = false)
public class TagModel extends AuditModel {
    @Id
    @Column(name = "TAG")
    private String tag;

}