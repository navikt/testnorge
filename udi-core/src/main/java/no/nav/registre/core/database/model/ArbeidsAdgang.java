package no.nav.registre.core.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.ArbeidOmfangKategori;
import no.udi.mt_1067_nav_data.v1.ArbeidsadgangType;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ArbeidsAdgang {

    @GeneratedValue
    @Id
    private Long id;

    private JaNeiUavklart harArbeidsAdgang;

    private ArbeidsadgangType typeArbeidsadgang;

    private ArbeidOmfangKategori arbeidsOmfang;

    @Embedded
    private Periode periode;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_fnr", nullable = false)
    @JsonBackReference
    private Person person;


}
