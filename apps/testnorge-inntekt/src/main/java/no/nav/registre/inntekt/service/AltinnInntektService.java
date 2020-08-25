package no.nav.registre.inntekt.service;

import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.inntekt.consumer.rs.altinninntekt.AltinnInntektConsumer;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.RsInntektsmeldingRequest;
import no.nav.registre.inntekt.consumer.rs.dokmot.DokmotConsumer;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.InntektDokument;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.ProsessertInntektDokument;
import no.nav.registre.inntekt.factories.RsAltinnInntektsmeldingFactory;
import no.nav.registre.inntekt.factories.RsJoarkMetadataFactory;
import no.nav.registre.inntekt.provider.rs.requests.AltinnInntektsmeldingRequest;
import no.nav.registre.inntekt.repository.InntektsmedlingRepository;
import no.nav.registre.inntekt.repository.model.InntektsmeldingModel;

@Slf4j
@Service
@RequiredArgsConstructor
public class AltinnInntektService {

    private final AltinnInntektConsumer altinnInntektConsumer;
    private final DokmotConsumer dokmotConsumer;
    private final InntektsmedlingRepository repository;

    public List<ProsessertInntektDokument> opprettInntektsmelding(
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
        var xmlString = altinnInntektConsumer.getInntektsmeldingXml201812(RsAltinnInntektsmeldingFactory.create(rsInntektsmelding, ident));
        log.trace(xmlString);
        InntektsmeldingModel model = repository.save(new InntektsmeldingModel(xmlString, ident));

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
