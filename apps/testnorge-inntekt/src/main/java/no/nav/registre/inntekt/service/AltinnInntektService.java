package no.nav.registre.inntekt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.AltinnInntektConsumer;
import no.nav.registre.inntekt.consumer.rs.dokmot.DokmotConsumer;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.RsInntektsmeldingRequest;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.InntektDokument;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.ProsessertInntektDokument;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.RsJoarkMetadata;
import no.nav.registre.inntekt.factories.RsAltinnInntektsmeldingFactory;
import no.nav.registre.inntekt.factories.RsJoarkMetadataFactory;
import no.nav.registre.inntekt.provider.rs.requests.AltinnInntektsmeldingRequest;
import org.springframework.stereotype.Service;


import javax.validation.ValidationException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AltinnInntektService {

    private final AltinnInntektConsumer altinnInntektConsumer;
    private final DokmotConsumer dokmotConsumer;


    public List<ProsessertInntektDokument> utfoerAltinnInntektMeldingRequest(
            String navCallId,
            AltinnInntektsmeldingRequest request
    ) {

        List<InntektDokument> dokumentListe = request.getInntekter()
                .stream()
                .map(melding -> lagInntektDokument(melding, request.getArbeidstakerFnr()))
                .collect(Collectors.toList());

        return dokmotConsumer.opprettJournalpost(request.getMiljoe(), dokumentListe, navCallId);
    }

    private InntektDokument lagInntektDokument(
            RsInntektsmeldingRequest rsInntektsmelding,
            String ident
    ) {
        var xmlString  = altinnInntektConsumer.getInntektsmeldingXml201812(RsAltinnInntektsmeldingFactory.create(rsInntektsmelding, ident));
        if (xmlString.equals("")) {
            throw new RuntimeException("Kunne ikke oversette inntektsmleding til XML.");
        }
        return InntektDokument.builder()
                .arbeidstakerFnr(ident)
                .datoMottatt(Date.from(rsInntektsmelding.getAvsendersystem().getInnsendingstidspunkt().atZone(ZoneId.systemDefault()).toInstant()))
                .virksomhetsnavn(getVirksomhetsnavn(rsInntektsmelding))
                .virksomhetsnummer(getVirksomhetsnummer(rsInntektsmelding))
                .metadata(RsJoarkMetadataFactory.create(rsInntektsmelding))
                .xml(xmlString)
                .build();
    }

    private String getVirksomhetsnavn(RsInntektsmeldingRequest inntekt) {
        try {
            return inntekt.getArbeidsgiver().getKontaktinformasjon().getKontaktinformasjonNavn();
        } catch (NullPointerException ignored) {}

        try {
            return inntekt.getArbeidsgiverPrivat().getKontaktinformasjon().getKontaktinformasjonNavn();
        } catch (NullPointerException e) {
            throw new ValidationException("Ingen arbeidsgiver med kontaktinformasjon oppgitt. Avbryter.");
        }
    }

    private String getVirksomhetsnummer(RsInntektsmeldingRequest inntekt) {
        try {
            return inntekt.getArbeidsgiver().getVirksomhetsnummer();
        } catch (NullPointerException ignored) {}
        try {
            return inntekt.getArbeidsgiverPrivat().getArbeidsgiverFnr();
        } catch (NullPointerException e) {
            throw new ValidationException("Virksomhetsnummer for arbeidsgiver ikke oppgitt. Avbryter.");
        }
    }
}
