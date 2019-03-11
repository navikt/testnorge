package no.nav.registre.tp.database.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name = "T_FORHOLD")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TForhold {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "forhold_seq")
    @SequenceGenerator(sequenceName = "T_FORHOLD_SEQ", name = "forhold_seq", allocationSize = 1)

    @Id
    private Integer forholdId;
    private Integer personId;
    private java.sql.Date datoSamtykkeGitt;
    private String tssEksternIdFk;
    private java.sql.Timestamp datoOpprettet;
    private String opprettetAv;
    private java.sql.Timestamp datoEndret;
    private String harUtlandPensj;
    private String endretAv;
    @Column(name = "K_KILDE_TP_T")
    private String kKildeTpT;
    private String versjon;
    private String erGyldig;
    @Column(name = "K_SAMTYKKE_SIM_T")
    private String kSamtykkeSimT;
    private String harSimulering;
    private String funkForholdId;
    private java.sql.Timestamp datoBrukFom;
    private java.sql.Timestamp datoBrukTom;

}
