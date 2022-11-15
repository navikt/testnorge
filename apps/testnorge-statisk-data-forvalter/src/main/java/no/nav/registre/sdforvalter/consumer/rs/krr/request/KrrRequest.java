package no.nav.registre.sdforvalter.consumer.rs.krr.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import no.nav.registre.sdforvalter.domain.Krr;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class KrrRequest {

    @JsonProperty("personident")
    private String fnr;

    @JsonProperty("gyldigFra")
    private String validFrom;

    @JsonProperty("reservert")
    private boolean reserved;

    @JsonProperty("epost")
    private String email;
    @JsonProperty("epostVerifisert")
    private String emailValidatedDate;
    @JsonProperty("epostOppdatert")
    private String emailUpdated;

    @JsonProperty("mobil")
    private String phone;
    @JsonProperty("mobilVerifisert")
    private String phoneValidatedDate;
    @JsonProperty("mobilOppdatert")
    private String phoneUpdated;

    @JsonProperty("sdpAdresse")
    private String sdpAddress;
    @JsonProperty("sdpLeverandoer")
    private Integer sdpProvider;


    public KrrRequest(Krr krr) {
        ZonedDateTime now = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        email = krr.getEmail();
        if (krr.isEmailValid()) {
            emailUpdated = now.toString();
            emailValidatedDate = now.toString();
        }
        phone = krr.getSms();
        if (krr.isSmsValid()) {
            phoneUpdated = now.toString();
            phoneValidatedDate = now.toString();
        }
        reserved = krr.isReserved();
        fnr = krr.getFnr();
        validFrom = now.toString();
        sdpAddress = null;
        sdpProvider = null;
    }


}
