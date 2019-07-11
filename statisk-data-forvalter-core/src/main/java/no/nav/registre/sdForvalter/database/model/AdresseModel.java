package no.nav.registre.sdForvalter.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "Adresse")
public class AdresseModel {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private String adresse;
    private String postnr;
    private String kommunenr;
    private String landkode;
    private String poststed;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinTable(name = "ereg_adresse",
            joinColumns = {@JoinColumn(name = "ereg_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "adreses_id", referencedColumnName = "id")})
    @JsonIgnore
    private EregModel eregModel;

}
