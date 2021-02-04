package no.nav.udistub.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
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
@Setter
@Getter
public class ArbeidsadgangUtvidet {

    @GeneratedValue
    @Id
    private Long id;

    private JaNeiUavklart harArbeidsadgang;

    private ArbeidsadgangType typeArbeidsadgang;

    private ArbeidOmfangKategori arbeidsomfang;

    @Embedded
    private Periode arbeidsadgangsperiode;

    private String forklaring;

    private String hjemmel;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

}
