package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.dovarkiv.v1;

import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Value
@Builder
public class InntektDokument {
    private final String arbeidstakerFnr;
    private final Date datoMottatt;
    private final String virksomhetsnavn;
    private final String virksomhetsnummer;
    private final RsJoarkMetadata metadata;
    private final String xml;
}
