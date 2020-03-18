package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.sdForvalter.consumer.rs.request.KrrRequest;
import no.nav.registre.sdForvalter.database.model.KrrModel;

@Value
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class Krr extends FasteData {
    @JsonProperty(required = true)
    private String fnr;
    @JsonProperty
    private String name;
    @JsonProperty
    private String email;
    @JsonProperty
    private String sms;
    @JsonProperty
    private boolean reserved;
    @JsonProperty
    private boolean sdp;
    @JsonProperty
    private boolean emailValid;
    @JsonProperty
    private boolean smsValid;

    public Krr(KrrModel model) {
        fnr = model.getFnr();
        name = model.getName();
        email = model.getEmail();
        sms = model.getSms();
        reserved = model.isReserved();
        sdp = model.isSdp();
        emailValid = model.isEmailValid();
        smsValid = model.isSmsValid();
    }
}