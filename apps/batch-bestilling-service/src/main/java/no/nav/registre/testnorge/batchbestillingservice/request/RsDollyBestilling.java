package no.nav.registre.testnorge.batchbestillingservice.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsDollyBestilling {

    @Schema(description = "Liste av miljøer bestillingen skal deployes til")
    private List<String> environments;

    @Schema(description = "Navn på malbestillling")
    private String malBestillingNavn;
    private Object pdlforvalter;
    private Object pdldata;
    private Object krrstub;
    private List<Object> instdata;
    private List<Object> aareg;
    private List<Object> sigrunstub;
    private Object inntektstub;
    private Object arenaforvalter;
    private Object udistub;
    private Object pensjonforvalter;
    private Object inntektsmelding;
    private Object brregstub;
    private Object dokarkiv;
    private Object sykemelding;
    private Object tpsMessaging;
    private Object bankkonto;
    private Object skjerming;
}
