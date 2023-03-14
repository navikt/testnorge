package no.nav.udistub.database.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.ArbeidOmfangKategori;
import no.udi.mt_1067_nav_data.v1.ArbeidsadgangType;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Arbeidsadgang {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator")
    @SequenceGenerator(name = "sequence_generator", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Id
    private Long id;

    private JaNeiUavklart harArbeidsAdgang;

    private ArbeidsadgangType typeArbeidsadgang;

    private ArbeidOmfangKategori arbeidsOmfang;

    @Embedded
    private Periode periode;

    private String forklaring;

    private String hjemmel;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

}
