package no.nav.registre.sdForvalter.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "EREG")
public class EregModel extends AuditModel {

    @Id
    @GeneratedValue
    @JsonIgnore
    Long id;

    @NotNull
    @Column(unique = true)
    private String orgnr;

    @NotNull
    private String enhetstype;

    private String navn;
    private String epost;
    private String internetAdresse;

    private String naeringskode;

    private String parent;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "adresse", column = @Column(name = "forretnings_adresse")),
            @AttributeOverride(name = "postnr", column = @Column(name = "forretnings_postnr")),
            @AttributeOverride(name = "kommunenr", column = @Column(name = "forretnings_kommunenr")),
            @AttributeOverride(name = "landkode", column = @Column(name = "forretnings_landkode")),
            @AttributeOverride(name = "poststed", column = @Column(name = "forretnings_poststed")),
    })
    private AdresseModel forretningsAdresse;

    @Embedded
    private AdresseModel postadresse;

    @Column(name = "eksludert")
    private boolean excluded = false;
}
