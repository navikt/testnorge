package no.nav.registre.sdForvalter.consumer.rs.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

import no.nav.registre.sdForvalter.database.model.KrrModel;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DkifRequest {

    private String brukFom;
    private String brukTom;
    private String registrertAv;
    private String registrert;
    @JsonProperty("fnr")
    private String personIdOff;
    private Boolean reservasjon;
    private String mobil;
    private String mobilOppdatert;
    private String mobilVerifisert;
    private String epost;
    private String epostOppdatert;
    private String epostVerifisert;
    private String postkasseAdresse;
    private String postkasseLevAdresse;
    private String x509SertifikatBase64Encoded;

    public DkifRequest(KrrModel krrModel) {
        registrertAv = "Faste data";
        personIdOff = krrModel.getFnr();
        reservasjon = krrModel.isReserved();
        mobil = krrModel.getSms();
        epost = krrModel.getEmail();
        if (krrModel.isEmailValid()) {
            epostVerifisert = new Timestamp(new Date().getTime()).toString();
        }
        if (krrModel.isSmsValid()) {
            mobilVerifisert = new Timestamp(new Date().getTime()).toString();
        }

    }
}
