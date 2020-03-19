package no.nav.dolly.bestilling.pdlforvalter;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.reverse;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.StausResponse.DONE;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDoedsfall;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFamilierelasjon;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFoedsel;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKjoenn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlNavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpprettPerson;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlStatsborgerskap;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlTelefonnummer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pdlforvalter.Pdldata;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.domain.resultset.tpsf.RsBarnRequest;
import no.nav.dolly.domain.resultset.tpsf.RsPartnerRequest;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.service.TpsfPersonCache;

@Slf4j
@Order(1)
@Service
@RequiredArgsConstructor
public class PdlForvalterClient implements ClientRegister {

    public enum StausResponse {DONE, PENDING, DELETING}

    public static final String KILDE = "Dolly";
    public static final String SYNTH_ENV = "q2";
    public static final String KONTAKTINFORMASJON_DOEDSBO = "KontaktinformasjonForDoedsbo";
    public static final String UTENLANDS_IDENTIFIKASJONSNUMMER = "UtenlandskIdentifikasjonsnummer";
    public static final String FALSK_IDENTITET = "FalskIdentitet";
    public static final String PDL_FORVALTER = "PdlForvalter";

    private static final String UKJENT = "U";
    private static final String HENDELSE_ID = "hendelseId";
    private static final int MAX_COUNT = 30;
    private static final int TIMEOUT = 100;

    private static final String SEND_ERROR = "Feilet å sende %s for ident %s til PDL-forvalter";
    private static final String SEND_ERROR_2 = SEND_ERROR + ": %s";

    private final PdlForvalterConsumer pdlForvalterConsumer;
    private final TpsfPersonCache tpsfPersonCache;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Timed(name = "providers", tags = { "operation", "gjenopprettPdlForvalter" })
    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (bestilling.getEnvironments().contains(SYNTH_ENV) || nonNull(bestilling.getPdlforvalter())) {

            StringBuilder status = new StringBuilder();

            if (bestilling.getEnvironments().contains(SYNTH_ENV)) {

                hentTpsPersondetaljer(tpsPerson, bestilling.getTpsf(), isOpprettEndre);
                if (!isOpprettEndre) {
                    sendDeleteIdent(tpsPerson);
                }
                sendPdlPersondetaljer(tpsPerson, status, isOpprettEndre);

                if (nonNull(bestilling.getPdlforvalter())) {
                    Pdldata pdldata = mapperFacade.map(bestilling.getPdlforvalter(), Pdldata.class);
                    sendUtenlandsid(pdldata, tpsPerson.getHovedperson(), status);
                    sendDoedsbo(pdldata, tpsPerson.getHovedperson(), status);
                    sendFalskIdentitet(pdldata, tpsPerson.getHovedperson(), status);
                }

            } else {

                status.append('$')
                        .append(PDL_FORVALTER)
                        .append("&Feil: Bestilling ble ikke sendt til Persondataløsningen (PDL) da miljø '")
                        .append(SYNTH_ENV)
                        .append("' ikke er valgt");
            }

            if (status.length() > 1) {
                progress.setPdlforvalterStatus(status.substring(1));
            }
        }
    }

    @Override
    public void release(List<String> identer) {

        try {
            identer.forEach(pdlForvalterConsumer::deleteIdent);

        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void hentTpsPersondetaljer(TpsPerson tpsPerson, RsTpsfUtvidetBestilling tpsfUtvidetBestilling, boolean isOpprettEndre) {

        tpsfPersonCache.fetchIfEmpty(tpsPerson);

        if (nonNull(tpsPerson.getPerson(tpsPerson.getHovedperson()))) {
            Person hovedperson = tpsPerson.getPerson(tpsPerson.getHovedperson());
            if (nonNull(tpsfUtvidetBestilling)) {
                if (UKJENT.equals(tpsfUtvidetBestilling.getKjonn())) {
                    hovedperson.setKjonn(UKJENT);
                }
                if (nonNull(tpsfUtvidetBestilling.getRelasjoner())) {
                    List partnereRequest = newArrayList(tpsfUtvidetBestilling.getRelasjoner().getPartnere());
                    Iterator<RsPartnerRequest> partnere = reverse(partnereRequest).iterator();
                    Iterator<RsBarnRequest> barn = tpsfUtvidetBestilling.getRelasjoner().getBarn().iterator();
                    hovedperson.getRelasjoner().forEach(relasjon -> {
                        if ((!isOpprettEndre ||
                                tpsPerson.getNyePartnereOgBarn().contains(relasjon.getPersonRelasjonMed().getIdent())) &&
                                (isKjonnUkjent(relasjon, partnere, barn) &&
                                        nonNull(tpsPerson.getPerson(relasjon.getPersonRelasjonMed().getIdent())))) {
                            tpsPerson.getPerson(relasjon.getPersonRelasjonMed().getIdent()).setKjonn(UKJENT);
                        }
                    });
                }
            }
        }
        tpsPerson.getPersondetaljer().forEach(person ->
                person.getRelasjoner().forEach(relasjon ->
                        relasjon.setPersonRelasjonTil(tpsPerson.getPerson(relasjon.getPersonRelasjonMed().getIdent()))
                )
        );
    }

    private static boolean isKjonnUkjent(Relasjon relasjon, Iterator<RsPartnerRequest> partnere,
            Iterator<RsBarnRequest> barn) {

        return relasjon.isPartner() && partnere.next().isKjonnUkjent() ||
                relasjon.isBarn() && barn.next().isKjonnUkjent();
    }

    private void sendPdlPersondetaljer(TpsPerson tpsPerson, StringBuilder status, boolean isOpprettEndre) {

        status.append('$').append(PDL_FORVALTER);

        try {
            tpsPerson.getPersondetaljer().forEach(person -> {
                if (!isOpprettEndre || tpsPerson.getNyePartnereOgBarn().contains(person.getIdent())) {
                    sendOpprettPerson(person);
                    sendFoedselsmelding(person);
                    sendNavn(person);
                    sendKjoenn(person);
                    sendAdressebeskyttelse(person);
                    sendStatsborgerskap(person);
                    sendFamilierelasjoner(person);
                    sendDoedsfall(person);
                    sendTelefonnummer(person);
                }
            });

            syncMedPdl(tpsPerson.getHovedperson(), status);

        } catch (DollyFunctionalException e) {

            status.append('&').append(e.getMessage().replaceAll(",", ";"));
        }
    }

    private void syncMedPdl(String ident, StringBuilder status) {

        int count = 0;
        try {
            while (count++ < MAX_COUNT && !DONE.name().equals(pdlForvalterConsumer.getPersonstatus(ident).getBody().get("status").asText())) {
                Thread.sleep(TIMEOUT);
            }
        } catch (InterruptedException e) {
            log.error("Sync mot PDL-forvalter ble avbrutt.");
        } catch (RuntimeException e) {
            log.error("Feilet å lese personstatus for ident {} fra PDL-forvalter.", ident, e);
        }

        if (count < MAX_COUNT) {
            status.append("&OK");
            log.info("Synkronisering mot PDL-forvalter tok {} ms.", count * TIMEOUT);
        } else {
            status.append("&Synkronisering av person i PDL tok for lang tid");
            log.warn("Synkronisering mot PDL-forvalter gitt opp etter 1000 ms.");
        }
    }

    private void sendOpprettPerson(Person person) {

        BiFunction<PdlOpprettPerson, String, ResponseEntity> opprettPerson = (struct, ident) -> pdlForvalterConsumer.postOpprettPerson(struct, ident);
        sendToPdl(opprettPerson, mapperFacade.map(person, PdlOpprettPerson.class), person.getIdent(), "opprett person");
    }

    private void sendNavn(Person person) {

        BiFunction<PdlNavn, String, ResponseEntity> sendNavn = (struct, ident) -> pdlForvalterConsumer.postNavn(struct, ident);
        sendToPdl(sendNavn, mapperFacade.map(person, PdlNavn.class), person.getIdent(), "navn");
    }

    private void sendKjoenn(Person person) {

        BiFunction<PdlKjoenn, String, ResponseEntity> sendKjoenn = (struct, ident) -> pdlForvalterConsumer.postKjoenn(struct, ident);
        sendToPdl(sendKjoenn, mapperFacade.map(person, PdlKjoenn.class), person.getIdent(), "kjønn");
    }

    private void sendAdressebeskyttelse(Person person) {

        BiFunction<PdlAdressebeskyttelse, String, ResponseEntity> sendAdressebeskyttelse = (struct, ident) -> pdlForvalterConsumer.postAdressebeskyttelse(struct, ident);
        sendToPdl(sendAdressebeskyttelse, mapperFacade.map(person, PdlAdressebeskyttelse.class), person.getIdent(), "adressebeskyttelse");
    }

    private void sendFamilierelasjoner(Person person) {
        person.getRelasjoner().forEach(relasjon -> {
            if (!relasjon.isPartner()) {
                BiFunction<PdlFamilierelasjon, String, ResponseEntity> sendFamilierelasjon = (struct, ident) -> pdlForvalterConsumer.postFamilierelasjon(struct, ident);
                sendToPdl(sendFamilierelasjon, mapperFacade.map(relasjon, PdlFamilierelasjon.class), person.getIdent(), "familierelasjon");
            }
        });
    }

    private void sendDoedsfall(Person person) {

        if (nonNull(person.getDoedsdato())) {

            BiFunction<PdlDoedsfall, String, ResponseEntity> sendDoedsmelding = (struct, ident) -> pdlForvalterConsumer.postDoedsfall(struct, ident);
            sendToPdl(sendDoedsmelding, mapperFacade.map(person, PdlDoedsfall.class), person.getIdent(), "dødsmelding");
        }
    }

    private void sendStatsborgerskap(Person person) {

        person.getStatsborgerskap().forEach(statsborgerskap -> {
            BiFunction<PdlStatsborgerskap, String, ResponseEntity> sendStatsborgerskap = (struct, ident) -> pdlForvalterConsumer.postStatsborgerskap(struct, ident);
            sendToPdl(sendStatsborgerskap, mapperFacade.map(statsborgerskap, PdlStatsborgerskap.class), person.getIdent(), "adressebeskyttelse");
        });
    }

    private void sendFoedselsmelding(Person person) {

        BiFunction<PdlFoedsel, String, ResponseEntity> sendFoedselsmelding = (struct, ident) -> pdlForvalterConsumer.postFoedsel(struct, ident);
        sendToPdl(sendFoedselsmelding, mapperFacade.map(person, PdlFoedsel.class), person.getIdent(), "fødselsmelding");
    }

    private void sendTelefonnummer(Person person) {

        BiFunction<PdlTelefonnummer.Entry, String, ResponseEntity> sendTelefonummer = (struct, ident) -> pdlForvalterConsumer.postTelefonnummer(struct, ident);
        PdlTelefonnummer telefonnumre = mapperFacade.map(person, PdlTelefonnummer.class);
        telefonnumre.getTelfonnumre().forEach(telefonnummer ->
                sendToPdl(sendTelefonummer, telefonnummer, person.getIdent(), "telefonnummer")
        );
    }

    private void sendUtenlandsid(Pdldata pdldata, String ident, StringBuilder status) {

        if (nonNull(pdldata) && nonNull(pdldata.getUtenlandskIdentifikasjonsnummer())) {
            try {
                appendName(UTENLANDS_IDENTIFIKASJONSNUMMER, status);

                List<PdlUtenlandskIdentifikasjonsnummer> utenlandskId = pdldata.getUtenlandskIdentifikasjonsnummer();
                utenlandskId.forEach(id -> {
                    id.setKilde(nullcheckSetDefaultValue(id.getKilde(), KILDE));

                    ResponseEntity<JsonNode> response =
                            pdlForvalterConsumer.postUtenlandskIdentifikasjonsnummer(id, ident);

                    appendOkStatus(response.getBody(), status);
                });

            } catch (RuntimeException exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendDoedsbo(Pdldata pdldata, String ident, StringBuilder status) {

        if (nonNull(pdldata) && nonNull(pdldata.getKontaktinformasjonForDoedsbo())) {
            try {
                appendName(KONTAKTINFORMASJON_DOEDSBO, status);

                ResponseEntity<JsonNode> response =
                        pdlForvalterConsumer.postKontaktinformasjonForDoedsbo(pdldata.getKontaktinformasjonForDoedsbo(), ident);

                appendOkStatus(response.getBody(), status);

            } catch (RuntimeException exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendFalskIdentitet(Pdldata pdldata, String ident, StringBuilder status) {

        if (nonNull(pdldata) && nonNull(pdldata.getFalskIdentitet())) {
            try {
                appendName(FALSK_IDENTITET, status);

                ResponseEntity<JsonNode> response = pdlForvalterConsumer.postFalskIdentitet(pdldata.getFalskIdentitet(), ident);

                appendOkStatus(response.getBody(), status);

            } catch (RuntimeException exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendDeleteIdent(TpsPerson tpsPerson) {

        try {
            pdlForvalterConsumer.deleteIdent(tpsPerson.getHovedperson());
            tpsPerson.getPartnere().forEach(pdlForvalterConsumer::deleteIdent);
            tpsPerson.getBarn().forEach(pdlForvalterConsumer::deleteIdent);

        } catch (HttpClientErrorException e) {

            if (!HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                log.error(e.getMessage(), e);
            }
        } catch (RuntimeException e) {

            log.error(e.getMessage(), e);
        }
    }

    private void sendToPdl(BiFunction pdlConsumerFunction, Object struct, String ident, String beskrivelse) {

        try {
            pdlConsumerFunction.apply(struct, ident);

        } catch (HttpClientErrorException e) {
            log.error(format(SEND_ERROR_2, beskrivelse, ident, e.getResponseBodyAsString()));
            throw new DollyFunctionalException(format(SEND_ERROR_2, beskrivelse, ident,
                    errorStatusDecoder.decodeRuntimeException(e)), e);

        } catch (RuntimeException e) {
            log.error(format(SEND_ERROR, beskrivelse, ident), e);
            throw new DollyFunctionalException(format(SEND_ERROR_2, beskrivelse, ident,
                    errorStatusDecoder.decodeRuntimeException(e)), e);
        }
    }

    private static void appendName(String utenlandsIdentifikasjonsnummer, StringBuilder builder) {
        builder.append('$')
                .append(utenlandsIdentifikasjonsnummer);
    }

    private static void appendOkStatus(JsonNode jsonNode, StringBuilder builder) {
        builder.append("&OK");
        if (nonNull(jsonNode) && nonNull(jsonNode.get(HENDELSE_ID))) {
            builder.append(", ")
                    .append(HENDELSE_ID)
                    .append(": ")
                    .append(jsonNode.get(HENDELSE_ID));
        }
    }

    private void appendErrorStatus(RuntimeException exception, StringBuilder builder) {

        builder.append('&')
                .append(errorStatusDecoder.decodeRuntimeException(exception));
    }
}