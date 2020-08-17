package no.nav.registre.inntekt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.AltinnInntektConsumer;
import no.nav.registre.inntekt.consumer.rs.dokmot.DokmotConsumer;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.RsInntektsmeldingRequest;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.InntektDokument;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.InntektDokumentResponse;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.RsJoarkMetadata;
import no.nav.registre.inntekt.factories.InntektDokumentResponseFactory;
import no.nav.registre.inntekt.factories.RsAltinnInntektsmeldingFactory;
import no.nav.registre.inntekt.provider.rs.requests.AltinnInntektsmeldingRequest;
import no.nav.registre.inntekt.provider.rs.response.AltinnInntektResponse;
import no.nav.registre.inntekt.utils.ValidationException;
import no.nav.registre.testnorge.domain.dto.aordningen.arbeidsforhold.Arbeidsforhold;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AltinnInntektService {

    private final AaregService aaregService;
    private final AltinnInntektConsumer altinnInntektConsumer;
    private final DokmotConsumer dokmotConsumer;
    private final InntektDokumentResponseFactory responseFactory;


    public AltinnInntektResponse utfoerAltinnInntektMeldingRequest(
            String navCallId,
            AltinnInntektsmeldingRequest request,
            Boolean continueOnError,  // Fortsett å poste andre meldinger, selv om en melding feiler
            Boolean validerArbeidsforholdOpt,
            Boolean includeXml
    ) throws ValidationException {
        // validering
        List<RsInntektsmeldingRequest> inntektsmeldinger = new ArrayList<>(request.getInntekter().size());
        if (validerArbeidsforholdOpt == null || validerArbeidsforholdOpt) {
            inntektsmeldinger = validerArbeidsforhold(request.getArbeidstakerFnr(), request.getMiljoe(), request.getInntekter());
        }
        if (!continueOnError && (inntektsmeldinger.size() != request.getInntekter().size())) {
            throw new ValidationException("Følgende meldinger kunne ikke valideres: \n\n" + request.getInntekter().removeAll(inntektsmeldinger));
        }

        // send altinn meldinger til Joark
        var inntektDokumenter = preparerInntektDokumenter(request.getInntekter(), continueOnError, request.getJoarkMetadata(), request.getArbeidstakerFnr());
        if (inntektDokumenter.isEmpty()) {
            log.error("Ingen meldinger ble opprettet.");
            return new AltinnInntektResponse(request.getArbeidstakerFnr(), new ArrayList<>());
        }
        var sendtStatuser = sendTilJoark(inntektDokumenter, request.getMiljoe(), navCallId, continueOnError);

        // generer returnerbar rapport
        var rapporter = sendtStatuser.stream().map(m -> (includeXml) ? m : m.setXml(null)).collect(Collectors.toList());

        return null;
    }

    /**
     * Sjekker at identen faktisk har de oppgitte arbeidsforholdene i miljøet
     * @param ident FNR/DNR/BOST
     * @param miljoe miljoet der identen skal ha arbeidsforhold
     * @param inntektsmeldinger liste med oppgitte innteksmeldinger
     * @return liste av inntektsmeldinger med gyldige arbeidsforhold
     * @throws ValidationException kastes dersom ingen arbeidsforhold ble oppdaget
     */
    private List<RsInntektsmeldingRequest> validerArbeidsforhold(
            String ident,
            String miljoe,
            List<RsInntektsmeldingRequest> inntektsmeldinger
    ) throws ValidationException {
        // finn arbeidsforhold i miljø
        var oppdagedeArbeidsforhold = aaregService.hentArbeidsforhold(ident, miljoe);
        if (oppdagedeArbeidsforhold.isEmpty()) {
            throw new ValidationException("Kunne ikke finne noen arbeidsforhold for ident " + ident);
        }

        // sjekk arbeidsforhold i inntektsmeldinger stemmer overens med arbeidsforholdene som ble funnet
        var inntektsmeldingerMedGyldigeArbeidsforhold = new ArrayList<RsInntektsmeldingRequest>(inntektsmeldinger.size());
        for (var inntektsmelding : inntektsmeldinger) {
            for (var arbeidsforhold : oppdagedeArbeidsforhold) {
                if (gyldigArbeidsforholdId(arbeidsforhold, inntektsmelding.getArbeidsforhold().getArbeidsforholdId()) &&
                        !inntektsmeldingerMedGyldigeArbeidsforhold.contains(inntektsmelding)) {  // stygt! O(N^(MN))
                    inntektsmeldingerMedGyldigeArbeidsforhold.add(inntektsmelding);
                }
            }
        }
        return inntektsmeldingerMedGyldigeArbeidsforhold;
    }

    private boolean gyldigArbeidsforholdId(
            Arbeidsforhold aaregArbeidsforhold,
            String arbeidsforholdId) {
        return (arbeidsforholdId.equals(aaregArbeidsforhold.getArbeidsforholdId()) ||
                arbeidsforholdId.equals(aaregArbeidsforhold.getNavArbeidsforholdId().toString()));
    }

    private List<InntektDokument> preparerInntektDokumenter(
            List<RsInntektsmeldingRequest> meldinger,
            boolean continueOnError,
            RsJoarkMetadata metadata,
            String ident
    ) throws ValidationException {
        var preparerteMeldinger = new ArrayList<InntektDokument>(meldinger.size());
        for (var inntektsmelding : meldinger) {

            var preparertMelding = lagInntektDokument(inntektsmelding, ident, metadata);
            if (Objects.isNull(preparertMelding) && !continueOnError) {
                log.error("Feil ved oversettelse av inntektsmeldinger. Avslutter.");
                return new ArrayList<>();
            }
            preparerteMeldinger.add(preparertMelding);
        }
        return preparerteMeldinger;
    }

    private List<InntektDokumentResponse> sendTilJoark(
            List<InntektDokument> inntektDokumenter,
            String miljoe,
            String navCallId,
            boolean continueOnError
    ) {
        var opprettedeInntektsmeldinger = dokmotConsumer.opprettJournalpost(miljoe, inntektDokumenter, navCallId);
        return opprettedeInntektsmeldinger.stream().map(respons ->
                new InntektDokumentResponse(respons.getJournalpostId(), respons.getDokumentInfoId(), respons.getXml()))
                .collect(Collectors.toList());
    }


    // Hører hjemme i en InntektDokumentFactory
    private InntektDokument lagInntektDokument(
            RsInntektsmeldingRequest rsInntektsmelding,
            String ident,
            RsJoarkMetadata metadata
    ) throws ValidationException, HttpServerErrorException {
        var xmlString = genererAltinnXml(rsInntektsmelding, ident);
        if (xmlString.equals("")) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Kunne ikke oversette inntektsmleding til XML.");
        }
        return InntektDokument.builder()
                .arbeidstakerFnr(ident)
                .datoMottatt(Date.from(rsInntektsmelding.getAvsendersystem().getInnsendingstidspunkt().atZone(ZoneId.systemDefault()).toInstant()))
                .virksomhetsnavn(getVirksomhetsnavn(rsInntektsmelding))
                .virksomhetsnummer(getVirksomhetsnummer(rsInntektsmelding))
                .metadata(metadata)
                .xml(xmlString)
                .build();
    }

    private String genererAltinnXml(RsInntektsmeldingRequest inntekt, String ident) {
        try {
            return altinnInntektConsumer.getInntektsmeldingXml201812(
                    RsAltinnInntektsmeldingFactory.create(inntekt, ident));
        } catch (RestClientResponseException e) {
            log.warn("En inntektsmelding kunne ikke bli oversatt til XML.", e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Feilet mot Altinn Inntekt.");
        }
    }

    private String getVirksomhetsnavn(RsInntektsmeldingRequest inntekt) throws ValidationException {
        try {
            return inntekt.getArbeidsgiver().getKontaktinformasjon().getKontaktinformasjonNavn();
        } catch (NullPointerException ignored) {}

        try {
            return inntekt.getArbeidsgiverPrivat().getKontaktinformasjon().getKontaktinformasjonNavn();
        } catch (NullPointerException e) {
            throw new ValidationException("Ingen arbeidsgiver med kontaktinformasjon oppgitt. Avbryter.");
        }
    }

    private String getVirksomhetsnummer(RsInntektsmeldingRequest inntekt) throws ValidationException{
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
