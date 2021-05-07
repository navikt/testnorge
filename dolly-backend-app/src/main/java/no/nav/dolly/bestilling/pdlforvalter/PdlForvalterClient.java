package no.nav.dolly.bestilling.pdlforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlBostedsadresseHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDeltBosted.PdlDelteBosteder;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDoedsfall;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFamilierelasjon;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFoedsel;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFolkeregisterpersonstatus;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlForeldreansvar;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFullmaktHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlInnflyttingHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKjoenn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresseHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlNavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpphold;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresseHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpprettPerson;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlStatsborgerskap;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlTelefonnummer;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlUtflyttingHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaalHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.SivilstandWrapper;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pdlforvalter.Pdldata;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.domain.resultset.tpsf.adresse.IdentHistorikk;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.service.DollyPersonCache;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.containsSynthEnv;
import static no.nav.dolly.domain.CommonKeysAndUtils.getSynthEnv;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

@Slf4j
@Order(1)
@Service
@RequiredArgsConstructor
public class PdlForvalterClient implements ClientRegister {

    public static final String KONTAKTINFORMASJON_DOEDSBO = "KontaktinformasjonForDoedsbo";
    public static final String UTENLANDS_IDENTIFIKASJONSNUMMER = "UtenlandskIdentifikasjonsnummer";
    public static final String FALSK_IDENTITET = "FalskIdentitet";
    public static final String PDL_FORVALTER = "PdlForvalter";

    private final PdlForvalterConsumer pdlForvalterConsumer;
    private final DollyPersonCache dollyPersonCache;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    private static void appendName(String utenlandsIdentifikasjonsnummer, StringBuilder builder) {
        builder.append('$')
                .append(utenlandsIdentifikasjonsnummer);
    }

    private static void appendOkStatus(StringBuilder builder) {
        builder.append("&OK");
    }

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (containsSynthEnv(bestilling.getEnvironments()) || nonNull(bestilling.getPdlforvalter())) {

            StringBuilder status = new StringBuilder();

            if (containsSynthEnv(bestilling.getEnvironments())) {

                hentPersondetaljer(dollyPerson);
                sendDeleteIdent(dollyPerson);
                sendPdlPersondetaljer(bestilling, dollyPerson, status);

                if (nonNull(bestilling.getPdlforvalter())) {
                    Pdldata pdldata = mapperFacade.map(bestilling.getPdlforvalter(), Pdldata.class);
                    sendUtenlandsid(pdldata, dollyPerson.getHovedperson(), status);
                    sendDoedsbo(pdldata, dollyPerson.getHovedperson(), status);
                    sendFalskIdentitet(pdldata, dollyPerson.getHovedperson(), status);
                }

            } else {

                status.append('$')
                        .append(PDL_FORVALTER)
                        .append("&Feil: Bestilling ble ikke sendt til Persondataløsningen (PDL) da ingen av miljøene '")
                        .append(getSynthEnv())
                        .append("' er valgt");
            }

            if (status.length() > 1) {
                progress.setPdlforvalterStatus(encodeStatus(status.substring(1)));
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

   @Override
   public boolean isTestnorgeRelevant() {
        return false;
   }

    private void hentPersondetaljer(DollyPerson dollyPerson) {

        dollyPersonCache.fetchIfEmpty(dollyPerson);

        dollyPerson.getPersondetaljer()
                .forEach(person -> person.getRelasjoner().forEach(relasjon ->
                        relasjon.setPersonRelasjonTil(dollyPerson.getPerson(relasjon.getPersonRelasjonMed().getIdent()))));
    }

    private void sendPdlPersondetaljer(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, StringBuilder status) {

        status.append('$').append(PDL_FORVALTER);

        try {
            // Send historiske personer først
            dollyPerson.getPersondetaljer().stream()
                    .filter(person -> dollyPerson.getIdenthistorikk().stream().anyMatch(historisk -> historisk.equals(person.getIdent())))
                    .forEach(person ->
                        sendArtifacter(bestilling, dollyPerson, person));
            // Send øvrige personer
            dollyPerson.getPersondetaljer().stream()
                    .filter(person -> dollyPerson.getIdenthistorikk().stream().noneMatch(historisk -> historisk.equals(person.getIdent())))
                    .forEach(person ->
                            sendArtifacter(bestilling, dollyPerson, person));

            status.append("&OK");

        } catch (DollyFunctionalException e) {

            status.append('&').append(e.getMessage().replace(',', ';').replace(':', '='));

        } catch (RuntimeException e) {

            status.append('&')
                    .append(errorStatusDecoder.decodeRuntimeException(e));

        } catch (Exception e) {

            status.append("&Feil= Teknisk feil, se logg!");
            log.error(e.getMessage(), e);
        }
    }

    private void sendArtifacter(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, Person person) {
        sendOpprettPerson(person, dollyPerson);
        sendFoedselsmelding(person);
        sendNavn(person);
        sendKjoenn(person);
        sendAdressebeskyttelse(person);
        sendOppholdsadresse(person);
        sendKontaktadresse(person);
        sendBostedadresse(person);
        sendDeltBosted(person);
        sendInnflytting(person);
        sendUtflytting(person);
        sendFolkeregisterpersonstatus(person);
        sendStatsborgerskap(person);
        sendFamilierelasjoner(person);
        sendForeldreansvar(person);
        sendSivilstand(person);
        sendTelefonnummer(person);
        sendDoedsfall(person);
        sendOpphold(bestilling, person);
        sendVergemaal(person);
        sendFullmakt(person);
    }

    private void sendOpphold(RsDollyUtvidetBestilling bestilling, Person person) {

        if (nonNull(bestilling.getUdistub()) && nonNull(bestilling.getUdistub().getOppholdStatus())) {
            pdlForvalterConsumer.postOpphold(mapperFacade.map(bestilling.getUdistub(), PdlOpphold.class), person.getIdent());
        }
    }

    private void sendOpprettPerson(Person person, DollyPerson dollyPerson) {

        if (dollyPerson.getPerson(dollyPerson.getHovedperson()).getIdentHistorikk().stream()
                .map(IdentHistorikk::getAliasPerson)
                .noneMatch(histPerson -> histPerson.getIdent().equals(person.getIdent()))) {

            pdlForvalterConsumer.postOpprettPerson(mapperFacade.map(person, PdlOpprettPerson.class), person.getIdent());
        }
    }

    private void sendNavn(Person person) {

        if (!person.isDoedFoedt()) {
            pdlForvalterConsumer.postNavn(mapperFacade.map(person, PdlNavn.class), person.getIdent());
        }
    }

    private void sendKjoenn(Person person) {
        pdlForvalterConsumer.postKjoenn(mapperFacade.map(person, PdlKjoenn.class), person.getIdent());
    }

    private void sendAdressebeskyttelse(Person person) {

        if ("SPSF".equals(person.getSpesreg()) || "SPFO".equals(person.getSpesreg()) || "SFU".equals(person.getSpesreg())) {
            pdlForvalterConsumer.postAdressebeskyttelse(mapperFacade.map(person, PdlAdressebeskyttelse.class),
                    person.getIdent());
        }
    }

    private void sendFamilierelasjoner(Person person) {

        person.getRelasjoner().forEach(relasjon -> {
            if (!relasjon.isPartner() && nonNull(relasjon.getPersonRelasjonTil())) {
                pdlForvalterConsumer.postFamilierelasjon(mapperFacade.map(relasjon, PdlFamilierelasjon.class),
                        person.getIdent());
            }
        });
    }

    private void sendForeldreansvar(Person person) {

        if (!person.isMyndig()) {
            boolean fellesAnsvar = person.getRelasjoner().stream().filter(Relasjon::isForelder).count() == 2;
            person.getRelasjoner().forEach(relasjon -> {
                if (relasjon.isForelder()) {
                    relasjon.setFellesAnsvar(fellesAnsvar);
                    pdlForvalterConsumer.postForeldreansvar(
                            mapperFacade.map(relasjon, PdlForeldreansvar.class), person.getIdent());
                }
            });
        }
    }

    private void sendSivilstand(Person person) {

        if (person.isMyndig() && person.getSivilstander().isEmpty()) {
            pdlForvalterConsumer.postSivilstand(mapperFacade.map(person, PdlSivilstand.class), person.getIdent());

        } else {
            person.getSivilstander().forEach(sivilstand ->
                    pdlForvalterConsumer.postSivilstand(
                            mapperFacade.map(SivilstandWrapper.builder()
                                    .person(person)
                                    .sivilstand(sivilstand)
                                    .build(), PdlSivilstand.class), person.getIdent()));
        }
    }

    private void sendDoedsfall(Person person) {

        if (nonNull(person.getDoedsdato())) {
            pdlForvalterConsumer.postDoedsfall(mapperFacade.map(person, PdlDoedsfall.class),
                    person.getIdent());
        }
    }

    private void sendStatsborgerskap(Person person) {

        person.getStatsborgerskap().forEach(statsborgerskap -> pdlForvalterConsumer.postStatsborgerskap(mapperFacade.map(statsborgerskap, PdlStatsborgerskap.class),
                person.getIdent()));
    }

    private void sendFoedselsmelding(Person person) {

        pdlForvalterConsumer.postFoedsel(mapperFacade.map(person, PdlFoedsel.class), person.getIdent());
    }

    private void sendTelefonnummer(Person person) {

        PdlTelefonnummer telefonnumre = mapperFacade.map(person, PdlTelefonnummer.class);
        if (nonNull(telefonnumre)) {
            telefonnumre.getTelfonnumre().forEach(telefonnummer -> pdlForvalterConsumer.postTelefonnummer(telefonnummer, person.getIdent()));
        }
    }

    private void sendOppholdsadresse(Person person) {

        mapperFacade.map(person, PdlOppholdsadresseHistorikk.class).getPdlAdresser()
                .forEach(adresse -> pdlForvalterConsumer.postOppholdsadresse(adresse, person.getIdent()));
    }

    private void sendBostedadresse(Person person) {

        mapperFacade.map(person, PdlBostedsadresseHistorikk.class).getPdlAdresser()
                .forEach(adresse -> pdlForvalterConsumer.postBostedadresse(adresse, person.getIdent()));
    }

    private void sendFolkeregisterpersonstatus(Person person) {

        pdlForvalterConsumer.postFolkeregisterpersonstatus(
                mapperFacade.map(person, PdlFolkeregisterpersonstatus.class), person.getIdent());
    }

    private void sendKontaktadresse(Person person) {

        mapperFacade.map(person, PdlKontaktadresseHistorikk.class).getPdlAdresser()
                .forEach(adresse -> pdlForvalterConsumer.postKontaktadresse(adresse, person.getIdent()));
    }

    private void sendDeltBosted(Person person) {

        mapperFacade.map(person, PdlDelteBosteder.class).getDelteBosteder()
                .forEach(adresse -> pdlForvalterConsumer.postDeltBosted(adresse, person.getIdent()));
    }

    private void sendInnflytting(Person person) {

        mapperFacade.map(person, PdlInnflyttingHistorikk.class).getInnflyttinger().forEach(innflytting ->
                pdlForvalterConsumer.postInnflytting(innflytting, person.getIdent()));
    }

    private void sendUtflytting(Person person) {

        mapperFacade.map(person, PdlUtflyttingHistorikk.class).getUtflyttinger().forEach(utflytting ->
                pdlForvalterConsumer.postUtflytting(utflytting, person.getIdent()));
    }

    private void sendVergemaal(Person person) {

        mapperFacade.map(person, PdlVergemaalHistorikk.class).getVergemaaler()
                .forEach(vergemaal -> pdlForvalterConsumer.postVergemaal(vergemaal, person.getIdent()));
    }

    private void sendFullmakt(Person person) {

        mapperFacade.map(person, PdlFullmaktHistorikk.class).getFullmakter()
                .forEach(fullmakt -> pdlForvalterConsumer.postFullmakt(fullmakt, person.getIdent()));
    }

    private void sendUtenlandsid(Pdldata pdldata, String ident, StringBuilder status) {

        if (nonNull(pdldata) && nonNull(pdldata.getUtenlandskIdentifikasjonsnummer())) {
            try {
                appendName(UTENLANDS_IDENTIFIKASJONSNUMMER, status);

                List<PdlUtenlandskIdentifikasjonsnummer> utenlandskId = pdldata.getUtenlandskIdentifikasjonsnummer();
                utenlandskId.forEach(id -> {
                    id.setKilde(nullcheckSetDefaultValue(id.getKilde(), CONSUMER));
                    pdlForvalterConsumer.postUtenlandskIdentifikasjonsnummer(id, ident);
                });
                appendOkStatus(status);
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
                pdlForvalterConsumer.postKontaktinformasjonForDoedsbo(pdldata.getKontaktinformasjonForDoedsbo(), ident);
                appendOkStatus(status);

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
                pdlForvalterConsumer.postFalskIdentitet(pdldata.getFalskIdentitet(), ident);
                appendOkStatus(status);

            } catch (RuntimeException exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendDeleteIdent(DollyPerson dollyPerson) {

        try {
            dollyPerson.getPersondetaljer().forEach(person -> pdlForvalterConsumer.deleteIdent(person.getIdent()));

        } catch (HttpClientErrorException e) {

            if (!HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                log.error(e.getMessage(), e);
            }
        } catch (RuntimeException e) {

            log.error(e.getMessage(), e);
        }
    }

    private void appendErrorStatus(RuntimeException exception, StringBuilder builder) {

        builder.append('&')
                .append(errorStatusDecoder.decodeRuntimeException(exception));
    }
}