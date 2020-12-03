package no.nav.registre.orgnrservice.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tags")
@EqualsAndHashCode(callSuper = false)
public class OrgnummerModel {

    @Id
    @Column(name = "ORGNUMMER")
    private String orgnummer;

    @Id
    @Column(name = "LEDIG")
    private boolean ledig;

    //Type?
}