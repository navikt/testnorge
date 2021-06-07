package no.nav.registre.testnav.inntektsmeldingservice.service;

import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnav.inntektsmeldingservice.consumer.DokmotConsumer;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.GenererInntektsmeldingConsumer;
import no.nav.registre.testnorge.libs.dto.dokarkiv.v1.InntektDokument;
import no.nav.registre.testnorge.libs.dto.dokarkiv.v1.ProsessertInntektDokument;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.RsInntektsmeldingRequest;
import no.nav.registre.testnav.inntektsmeldingservice.factories.RsAltinnInntektsmeldingFactory;
import no.nav.registre.testnav.inntektsmeldingservice.factories.RsJoarkMetadataFactory;
import no.nav.registre.testnav.inntektsmeldingservice.repository.InntektsmedlingRepository;
import no.nav.registre.testnav.inntektsmeldingservice.repository.model.InntektsmeldingModel;
import no.nav.registre.testnorge.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektsmeldingService {

    private final GenererInntektsmeldingConsumer genererInntektsmeldingConsumer;
    private final DokmotConsumer dokmotConsumer;
    private final InntektsmedlingRepository repository;

    public List<ProsessertInntektDokument> opprettInntektsmelding(
            String navCallId,
            InntektsmeldingRequest request
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
        var xmlString = genererInntektsmeldingConsumer.getInntektsmeldingXml201812(RsAltinnInntektsmeldingFactory.create(rsInntektsmelding, ident));
        log.trace(xmlString);
        InntektsmeldingModel model = repository.save(new InntektsmeldingModel());

        return InntektDokument.builder()
                .arbeidstakerFnr(ident)
                .datoMottatt(Date.from(rsInntektsmelding.getAvsendersystem().getInnsendingstidspunkt().atZone(ZoneId.systemDefault()).toInstant()))
                .virksomhetsnavn(getVirksomhetsnavn(rsInntektsmelding))
                .virksomhetsnummer(getVirksomhetsnummer(rsInntektsmelding))
                .metadata(RsJoarkMetadataFactory.create(rsInntektsmelding, model.getId()))
                .xml(xmlString)
                .build();
    }

    private String getVirksomhetsnavn(RsInntektsmeldingRequest inntekt) {
        if (nonNull(inntekt.getArbeidsgiver())) {
            return inntekt.getArbeidsgiver().getKontaktinformasjon().getKontaktinformasjonNavn();
        }
        if (nonNull(inntekt.getArbeidsgiverPrivat())) {
            return inntekt.getArbeidsgiverPrivat().getKontaktinformasjon().getKontaktinformasjonNavn();
        }

        throw new ValidationException("Ingen arbeidsgiver med kontaktinformasjon oppgitt. Avbryter.");
    }

    private String getVirksomhetsnummer(RsInntektsmeldingRequest inntekt) {
        if (nonNull(inntekt.getArbeidsgiver())) {
            return inntekt.getArbeidsgiver().getVirksomhetsnummer();
        }

        if (nonNull(inntekt.getArbeidsgiverPrivat())) {
            return inntekt.getArbeidsgiverPrivat().getArbeidsgiverFnr();
        }

        throw new ValidationException("Virksomhetsnummer for arbeidsgiver ikke oppgitt. Avbryter.");
    }
}
