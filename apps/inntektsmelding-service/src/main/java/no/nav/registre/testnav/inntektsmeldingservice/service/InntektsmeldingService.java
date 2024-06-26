package no.nav.registre.testnav.inntektsmeldingservice.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.DokmotConsumer;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.GenererInntektsmeldingConsumer;
import no.nav.registre.testnav.inntektsmeldingservice.controller.InntektsmeldingRequest;
import no.nav.registre.testnav.inntektsmeldingservice.factories.RsAltinnInntektsmeldingFactory;
import no.nav.registre.testnav.inntektsmeldingservice.factories.RsJoarkMetadataFactory;
import no.nav.registre.testnav.inntektsmeldingservice.repository.InntektsmeldingRepository;
import no.nav.registre.testnav.inntektsmeldingservice.repository.model.InntektsmeldingModel;
import no.nav.testnav.libs.dto.dokarkiv.v1.InntektDokument;
import no.nav.testnav.libs.dto.dokarkiv.v1.ProsessertInntektDokument;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.RsInntektsmeldingRequest;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektsmeldingService {

    private final GenererInntektsmeldingConsumer genererInntektsmeldingConsumer;
    private final DokmotConsumer dokmotConsumer;
    private final InntektsmeldingRepository repository;

    public List<ProsessertInntektDokument> opprettInntektsmelding(
            String navCallId,
            InntektsmeldingRequest request
    ) {
        var dokumentListe = request
                .inntekter()
                .stream()
                .map(melding -> lagInntektDokument(melding, request.arbeidstakerFnr()))
                .toList();
        return dokmotConsumer.opprettJournalpost(request.miljoe(), dokumentListe, navCallId);
    }

    private InntektDokument lagInntektDokument(
            RsInntektsmeldingRequest rsInntektsmelding,
            String ident
    ) {
        var xmlString = genererInntektsmeldingConsumer.getInntektsmeldingXml201812(RsAltinnInntektsmeldingFactory.create(rsInntektsmelding, ident));
        var model = repository.save(new InntektsmeldingModel());
        log.info("Inntektsmelding generert med id: {}.\n{}", model.getId(), xmlString);
        return InntektDokument
                .builder()
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
