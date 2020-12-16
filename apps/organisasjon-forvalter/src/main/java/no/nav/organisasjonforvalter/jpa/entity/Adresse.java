package no.nav.organisasjonforvalter.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Builder
@Table(name = "Adresse")
@NoArgsConstructor
@AllArgsConstructor
public class Adresse implements Serializable {


    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adresse_seq")
    @SequenceGenerator(name = "adresse_seq", sequenceName = "ADRESSE_SEQ", allocationSize = 1)
    private Long id;
    @Column(name = "organisasjon_id")
    private Long organisasjonId;
    @Column(name = "adressetype")
    private AdresseType adressetype;
    @Column(name = "adresse")
    private String adresse;
    @Column(name = "postnr")
    private String postnr;
    @Column(name = "kommunenr")
    private String kommunenr;
    @Column(name = "landkode")
    private String landkode;
    @Column(name = "vegadresse_id")
    private String vegadresseId;

    public enum AdresseType {FADR, PADR}
}
