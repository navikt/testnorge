package no.nav.registre.tp.database.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

@Entity(name = "T_YTELSE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TYtelse {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ytelse_seq")
    @SequenceGenerator(sequenceName = "T_YTELSE_SEQ", name = "ytelse_seq", allocationSize = 1)

    @JsonIgnore
    @Id
    private Integer ytelseId;

    @JsonProperty("dato_innm_ytel_fom")
    private java.sql.Date datoInnmYtelFom;

    //    @JsonIgnore
    @JsonProperty("k_ytelse_t")
    @Column(name = "K_YTELSE_T")
    //    @OneToOne(targetEntity = TKYtelseT.class, cascade = CascadeType.ALL)
    //    @JoinColumn(name = "T_K_YTELSE_T_K_YTELSE_T", referencedColumnName = "kYtelseT")
    private String kYtelseT;

    //    @JsonIgnore
    @JsonProperty("k_melding_t")
    //    @OneToOne(targetEntity = TKMeldingT.class, cascade = CascadeType.ALL)
    //    @JoinColumn(name = "T_K_MELDING_T_K_MELDING_T", referencedColumnName = "kMeldingT")
    @Column(name = "K_MELDING_T")
    private String kMeldingT;

    @JsonProperty("dato_ytel_iver_fom")
    private java.sql.Date datoYtelIverFom;
    @JsonProperty("dato_ytel_iver_tom")
    private java.sql.Date datoYtelIverTom;
    @JsonProperty("dato_opprettet")
    private java.sql.Timestamp datoOpprettet;
    @JsonProperty("opprettet_av")
    private String opprettetAv;
    @JsonProperty("dato_endret")
    private java.sql.Timestamp datoEndret;
    @JsonProperty("endret_av")
    private String endretAv;
    @JsonProperty("versjon")
    private String versjon;
    @JsonProperty("er_gyldig")
    private String erGyldig;

    @JsonProperty("funk_ytelse_id")
    private String funkYtelseId;
    @JsonProperty("dato_bruk_fom")
    private java.sql.Timestamp datoBrukFom;
    @JsonProperty("dato_bruk_tom")
    private java.sql.Timestamp datoBrukTom;

}
