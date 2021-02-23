package no.nav.registre.testnorge.synt.tiltakservice.controller.request;

import java.time.LocalDate;
import java.util.Collections;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.request.RettighetFinnTiltakRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinnTiltakRequest {

    private String fodselsnr;
    private String miljoe;
    private LocalDate fraDato;
    private LocalDate tilDato;
    private String tiltakAdminKode;
    private String tiltakKode;
    private Double tiltakProsentDeltid;
    private String tiltakVedtak;

    @JsonIgnore
    public RettighetFinnTiltakRequest getRettighetRequest() {
        var vedtak = NyttVedtakTiltak.builder()
                .tiltakAdminKode(tiltakAdminKode)
                .tiltakKode(tiltakKode)
                .tiltakProsentDeltid(tiltakProsentDeltid)
                .tiltakVedtak(tiltakVedtak)
                .tiltakYtelse("J")
                .build();
        vedtak.setFraDato(fraDato);
        vedtak.setTilDato(tilDato);

        var request = new RettighetFinnTiltakRequest(Collections.singletonList(vedtak));
        request.setPersonident(fodselsnr);
        request.setMiljoe(miljoe);
        return request;
    }

}
