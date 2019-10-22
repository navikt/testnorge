package no.nav.registre.aareg.consumer.ws.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RsAaregOppdaterRequest extends RsAaregOpprettRequest {

    private LocalDateTime rapporteringsperiode;
}
