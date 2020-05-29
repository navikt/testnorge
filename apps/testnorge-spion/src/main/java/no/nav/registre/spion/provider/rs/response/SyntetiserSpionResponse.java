package no.nav.registre.spion.provider.rs.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SyntetiserSpionResponse {

    String message;

    public SyntetiserSpionResponse(int antallIdenter, int antallVellykketSendinger) {
        String syntInfo = "Vedtak ble syntetisert for " + antallIdenter + " ident(er). ";
        String publishInfo = "Sending av vedtak til Kafka Topic var vellykket for " + antallVellykketSendinger
                + " av " + antallIdenter + " ident(er). ";
        this.message = syntInfo + publishInfo;
    }
}
