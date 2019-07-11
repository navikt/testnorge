package no.nav.registre.sdForvalter.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Embeddable;

//@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
//@Table(name = "Adresse")
@Embeddable
public class AdresseModel {

//    @Id
//    @GeneratedValue
//    @JsonIgnore
//    private Long id;

    private String adresse;
    private String postnr;
    private String kommunenr;
    private String landkode;
    private String poststed;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JsonIgnore
//    private EregModel eregModel;

}
