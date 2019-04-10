package no.nav.registre.sdForvalter.consumer.rs.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
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
    @JsonProperty("epostOppdatert")
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
    private int sdpProvider;


}
