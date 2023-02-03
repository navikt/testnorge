package no.nav.dolly.bestilling.pdlforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Order(1)
//@Service
@RequiredArgsConstructor
public class PdlForvalterClient implements ClientRegister {

    public static final String KONTAKTINFORMASJON_DOEDSBO = "KontaktinformasjonForDoedsbo";
    public static final String UTENLANDS_IDENTIFIKASJONSNUMMER = "UtenlandskIdentifikasjonsnummer";
    public static final String FALSK_IDENTITET = "FalskIdentitet";
    public static final String PDL_FORVALTER = "PdlForvalter";
    public static final String PERSON = "person";

    private final PdlForvalterConsumer pdlForvalterConsumer;
    private final DollyPersonCache dollyPersonCache;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final PdlDataConsumer pdlDataConsumer;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

//        if (progress.isTpsf() &&
//                (isNull(bestilling.getPdldata()) || isNull(bestilling.getPdldata().getOpprettNyPerson()))) {
//
//            var status = new StringBuilder();
//
//            hentPersondetaljer(dollyPerson);
//            sendDeleteIdent(dollyPerson);
//            sendPdlPersondetaljer(bestilling, dollyPerson, status);
//
//            if (nonNull(bestilling.getPdlforvalter())) {
//                Pdldata pdldata = mapperFacade.map(bestilling.getPdlforvalter(), Pdldata.class);
//                sendUtenlandsid(pdldata, dollyPerson.getHovedperson(), status);
//                sendDoedsbo(pdldata, dollyPerson.getHovedperson(), status);
//                sendFalskIdentitet(pdldata, dollyPerson.getHovedperson(), status);
//            }
//
//            if (status.length() > 1) {
//                progress.setPdlforvalterStatus(encodeStatus(status.substring(1)));
//            }
//        }
        return Flux.empty();
    }

    @Override
    public void release(List<String> identer) {

        // Sletting blir nå utført fra PersonService
    }

//    private PersonDTO getPdldataHovedIdent(String ident) {
//
//        var personer = pdlDataConsumer.getPersoner(List.of(ident)).block();
//        return personer.isEmpty() ? null :
//                personer.stream().findFirst().orElse(new FullPersonDTO()).getPerson();
//    }

//    private void hentPersondetaljer(DollyPerson dollyPerson) {
//
//        dollyPersonCache.fetchIfEmpty(dollyPerson);
//
//        dollyPerson.getPersondetaljer()
//                .forEach(person -> person.getRelasjoner().forEach(relasjon ->
//                        relasjon.setPersonRelasjonTil(dollyPerson.getPerson(relasjon.getPersonRelasjonMed().getIdent()))));
//    }
//
//    private void sendPdlPersondetaljer(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, StringBuilder status) {
//
//        status.append('$').append(PDL_FORVALTER);
//
//        try {
//            // Send historiske personer først
//            dollyPerson.getPersondetaljer().stream()
//                    .filter(person -> dollyPerson.getIdenthistorikk().stream().anyMatch(historisk -> historisk.equals(person.getIdent())))
//                    .forEach(person ->
//                            sendArtifacter(bestilling, dollyPerson, person, false));
//
//            // Send hovedperson
//            sendArtifacter(bestilling, dollyPerson, dollyPerson.getPerson(dollyPerson.getHovedperson()), true);
//
//            // Send øvrige personer
//            dollyPerson.getPersondetaljer().stream()
//                    .filter(person -> !person.getIdent().equals(dollyPerson.getHovedperson()))
//                    .filter(person -> dollyPerson.getIdenthistorikk().stream().noneMatch(historisk -> historisk.equals(person.getIdent())))
//                    .forEach(person ->
//                            sendArtifacter(bestilling, dollyPerson, person, false));
//
//            status.append("&OK");
//
//        } catch (DollyFunctionalException e) {
//
//            status.append('&').append(e.getMessage().replace(',', ';').replace(':', '='));
//            log.error(e.getMessage(), e);
//
//        } catch (RuntimeException e) {
//
//            status.append('&')
//                    .append(errorStatusDecoder.decodeThrowable(e));
//            log.error(e.getMessage(), e);
//
//        } catch (Exception e) {
//
//            status.append("&Feil= Teknisk feil, se logg!");
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    private void sendArtifacter(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, Person person, boolean erHovedperson) {
//
//        if (IdentType.FDAT != getIdentType(person.getIdent())) {
//            var pdldataHovedIdent = getPdldataHovedIdent(dollyPerson.getHovedperson());
//
//            sendOpprettPerson(person, dollyPerson);
//            sendFoedselsmelding(person);
//            sendNavn(person);
//            sendKjoenn(person);
//            sendOppholdsadresse(person, pdldataHovedIdent, erHovedperson);
//            sendKontaktadresse(person, pdldataHovedIdent, erHovedperson);
//            sendBostedadresse(person, pdldataHovedIdent, erHovedperson);
//            sendDeltBosted(person);
//            sendInnflytting(person, pdldataHovedIdent, erHovedperson);
//            sendUtflytting(person, pdldataHovedIdent, erHovedperson);
//            sendFolkeregisterpersonstatus(person, pdldataHovedIdent, erHovedperson);
//            sendStatsborgerskap(person, pdldataHovedIdent, erHovedperson);
//            sendForeldreBarnRelasjon(person);
//            sendForeldreansvar(person);
//            sendSivilstand(person);
//            sendTelefonnummer(person, pdldataHovedIdent, erHovedperson);
//            sendDoedsfall(person, pdldataHovedIdent, erHovedperson);
//            sendOpphold(bestilling, person);
//            sendVergemaal(person);
//            sendFullmakt(person);
//            sendDoedfoedtBarn(person);
//            sendSikkerhetstiltak(person, pdldataHovedIdent, erHovedperson);
//            sendAdressebeskyttelse(person, pdldataHovedIdent, erHovedperson);
//        }
//    }
//
//    private void sendOpphold(RsDollyUtvidetBestilling bestilling, Person person) {
//
//        if (nonNull(bestilling.getUdistub()) && nonNull(bestilling.getUdistub().getOppholdStatus())) {
//            pdlForvalterConsumer.postOpphold(mapperFacade.map(bestilling.getUdistub(), PdlOpphold.class), person.getIdent());
//        }
//    }
//
//    private void sendOpprettPerson(Person person, DollyPerson dollyPerson) {
//
//        if (dollyPerson.getPerson(dollyPerson.getHovedperson()).getIdentHistorikk().stream()
//                .map(IdentHistorikk::getAliasPerson)
//                .noneMatch(histPerson -> histPerson.getIdent().equals(person.getIdent()))) {
//
//            pdlForvalterConsumer.postOpprettPerson(mapperFacade.map(person, PdlOpprettPerson.class), person.getIdent());
//        }
//    }
//
//    private void sendNavn(Person person) {
//
//        if (!person.isDoedFoedt()) {
//            pdlForvalterConsumer.postNavn(mapperFacade.map(person, PdlNavn.class), person.getIdent());
//        }
//    }
//
//    private void sendKjoenn(Person person) {
//        pdlForvalterConsumer.postKjoenn(mapperFacade.map(person, PdlKjoenn.class), person.getIdent());
//    }
//
//    private void sendAdressebeskyttelse(Person person, PersonDTO persondata, boolean erHovedperson) {
//
//        if ((!erHovedperson || isNull(persondata) ||
//                persondata.getAdressebeskyttelse().isEmpty()) &&
//                ("SPSF".equals(person.getSpesreg()) || "SPFO".equals(person.getSpesreg()) || "SFU".equals(person.getSpesreg()))) {
//            pdlForvalterConsumer.postAdressebeskyttelse(mapperFacade.map(person, PdlAdressebeskyttelse.class),
//                    person.getIdent());
//        }
//    }
//
//    private void sendForeldreBarnRelasjon(Person person) {
//
//        mapperFacade.mapAsList(person.getRelasjoner(), PdlForelderBarnRelasjon.class).stream()
//                .filter(relasjon -> nonNull(relasjon.getMinRolleForPerson()))
//                .forEach(relasjon -> pdlForvalterConsumer.postForeldreBarnRelasjon(relasjon, person.getIdent()));
//    }
//
//    private void sendForeldreansvar(Person person) {
//
//        if (!person.isMyndig()) {
//            boolean fellesAnsvar = person.getRelasjoner().stream().filter(Relasjon::isForelder).count() == 2;
//            person.getRelasjoner().forEach(relasjon -> {
//                if (relasjon.isForelder()) {
//                    relasjon.setFellesAnsvar(fellesAnsvar);
//                    pdlForvalterConsumer.postForeldreansvar(
//                            mapperFacade.map(relasjon, PdlForeldreansvar.class), person.getIdent());
//                }
//            });
//        }
//    }
//
//    private void sendSivilstand(Person person) {
//
//        if (person.isMyndig() && person.getSivilstander().isEmpty()) {
//            pdlForvalterConsumer.postSivilstand(mapperFacade.map(person, PdlSivilstand.class), person.getIdent());
//
//        } else {
//            person.getSivilstander().forEach(sivilstand ->
//                    pdlForvalterConsumer.postSivilstand(
//                            mapperFacade.map(SivilstandWrapper.builder()
//                                    .person(person)
//                                    .sivilstand(sivilstand)
//                                    .build(), PdlSivilstand.class), person.getIdent()));
//        }
//    }
//
//    private void sendDoedsfall(Person person, PersonDTO persondata, boolean erHovedperson) {
//
//        if ((!erHovedperson || isNull(persondata) || persondata.getDoedsfall().isEmpty()) &&
//                nonNull(person.getDoedsdato())) {
//
//            pdlForvalterConsumer.postDoedsfall(mapperFacade.map(person, PdlDoedsfall.class),
//                    person.getIdent());
//        }
//    }
//
//    private void sendStatsborgerskap(Person person, PersonDTO persondata, boolean erHovedperson) {
//
//        if (!erHovedperson || isNull(persondata) || persondata.getStatsborgerskap().isEmpty()) {
//
//            person.getStatsborgerskap().forEach(statsborgerskap -> pdlForvalterConsumer.postStatsborgerskap(mapperFacade.map(statsborgerskap, PdlStatsborgerskap.class),
//                    person.getIdent()));
//        }
//    }
//
//    private void sendFoedselsmelding(Person person) {
//
//        pdlForvalterConsumer.postFoedsel(mapperFacade.map(person, PdlFoedsel.class), person.getIdent());
//    }
//
//    private void sendTelefonnummer(Person person, PersonDTO persondata, boolean erHovedperson) {
//
//        if (!erHovedperson || isNull(persondata) || persondata.getTelefonnummer().isEmpty()) {
//
//            PdlTelefonnummer telefonnumre = mapperFacade.map(person, PdlTelefonnummer.class);
//            if (nonNull(telefonnumre)) {
//                telefonnumre.getTelfonnumre().forEach(telefonnummer -> pdlForvalterConsumer.postTelefonnummer(telefonnummer, person.getIdent()));
//            }
//        }
//    }
//
//    private void sendOppholdsadresse(Person person, PersonDTO persondata, boolean erHovedperson) {
//
//        if (!erHovedperson || hasNoPdldataAdresse(persondata)) {
//
//            var context = new MappingContext.Factory().getContext();
//            context.setProperty(PERSON, person);
//            mapperFacade.map(person, PdlOppholdsadresseHistorikk.class, context).getPdlAdresser()
//                    .forEach(adresse -> pdlForvalterConsumer.postOppholdsadresse(adresse, person.getIdent()));
//        }
//    }
//
//    private void sendBostedadresse(Person person, PersonDTO persondata, boolean erHovedperson) {
//
//        if (!erHovedperson || hasNoPdldataAdresse(persondata)) {
//
//            var context = new MappingContext.Factory().getContext();
//            context.setProperty(PERSON, person);
//            mapperFacade.map(person, PdlBostedsadresseHistorikk.class, context).getPdlAdresser()
//                    .forEach(adresse -> pdlForvalterConsumer.postBostedadresse(adresse, person.getIdent()));
//        }
//    }
//
//    private void sendFolkeregisterpersonstatus(Person person, PersonDTO persondata, boolean erHovedperson) {
//
//        if (IdentType.BOST != getIdentType(person.getIdent()) &&
//                (!erHovedperson || isNull(persondata) || persondata.getFolkeregisterPersonstatus().isEmpty())) {
//
//            pdlForvalterConsumer.postFolkeregisterpersonstatus(
//                    mapperFacade.map(person, PdlFolkeregisterpersonstatus.class), person.getIdent());
//        }
//    }
//
//    private void sendKontaktadresse(Person person, PersonDTO persondata, boolean erHovedperson) {
//
//        if (!erHovedperson || hasNoPdldataAdresse(persondata)) {
//
//            var context = new MappingContext.Factory().getContext();
//            context.setProperty(PERSON, person);
//            mapperFacade.map(person, PdlKontaktadresseHistorikk.class, context).getPdlAdresser()
//                    .forEach(adresse -> pdlForvalterConsumer.postKontaktadresse(adresse, person.getIdent()));
//        }
//    }
//
//    private void sendDeltBosted(Person person) {
//
//        mapperFacade.map(person, PdlDelteBosteder.class).getDelteBosteder()
//                .forEach(adresse -> pdlForvalterConsumer.postDeltBosted(adresse, person.getIdent()));
//    }
//
//    private void sendInnflytting(Person person, PersonDTO persondata, boolean erHovedperson) {
//
//        if (!erHovedperson || isNull(persondata) || persondata.getInnflytting().isEmpty()) {
//
//            mapperFacade.map(person, PdlInnflyttingHistorikk.class).getInnflyttinger().forEach(innflytting ->
//                    pdlForvalterConsumer.postInnflytting(innflytting, person.getIdent()));
//        }
//    }
//
//    private void sendUtflytting(Person person, PersonDTO persondata, boolean erHovedperson) {
//
//        if (!erHovedperson || isNull(persondata) || persondata.getUtflytting().isEmpty()) {
//
//            mapperFacade.map(person, PdlUtflyttingHistorikk.class).getUtflyttinger().forEach(utflytting ->
//                    pdlForvalterConsumer.postUtflytting(utflytting, person.getIdent()));
//        }
//    }
//
//    private void sendVergemaal(Person person) {
//
//        mapperFacade.map(person, PdlVergemaalHistorikk.class).getVergemaaler()
//                .forEach(vergemaal -> pdlForvalterConsumer.postVergemaal(vergemaal, person.getIdent()));
//    }
//
//    private void sendFullmakt(Person person) {
//
//        mapperFacade.map(person, PdlFullmaktHistorikk.class).getFullmakter()
//                .forEach(fullmakt -> pdlForvalterConsumer.postFullmakt(fullmakt, person.getIdent()));
//    }
//
//    private void sendDoedfoedtBarn(Person person) {
//
//        mapperFacade.mapAsList(person.getRelasjoner(), PdlDoedfoedtBarn.class).stream()
//                .filter(hendelse -> nonNull(hendelse.getDato()))
//                .forEach(hendelse -> pdlForvalterConsumer.postDoedfoedtBarn(hendelse, person.getIdent()));
//    }
//
//    private void sendSikkerhetstiltak(Person person, PersonDTO persondata, boolean erHovedperson) {
//
//        if ((!erHovedperson || isNull(persondata) || persondata.getSikkerhetstiltak().isEmpty()) &&
//                isNotBlank(person.getTypeSikkerhetTiltak())) {
//
//            pdlForvalterConsumer.postSikkerhetstiltak(mapperFacade.map(person, PdlSikkerhetstiltak.class), person.getIdent());
//        }
//    }
//
//    private void sendUtenlandsid(Pdldata pdldata, String ident, StringBuilder status) {
//
//        if (nonNull(pdldata) && nonNull(pdldata.getUtenlandskIdentifikasjonsnummer())) {
//            try {
//                appendName(UTENLANDS_IDENTIFIKASJONSNUMMER, status);
//
//                List<PdlUtenlandskIdentifikasjonsnummer> utenlandskId = pdldata.getUtenlandskIdentifikasjonsnummer();
//                utenlandskId.forEach(id -> {
//                    id.setKilde(nullcheckSetDefaultValue(id.getKilde(), CONSUMER));
//                    id.setMaster(Master.FREG);
//                    pdlForvalterConsumer.postUtenlandskIdentifikasjonsnummer(id, ident);
//                });
//                appendOkStatus(status);
//            } catch (RuntimeException exception) {
//
//                appendErrorStatus(exception, status);
//                log.error(exception.getMessage(), exception);
//            }
//        }
//    }
//
//    private void sendDoedsbo(Pdldata pdldata, String ident, StringBuilder status) {
//
//        if (nonNull(pdldata) && nonNull(pdldata.getKontaktinformasjonForDoedsbo())) {
//            try {
//                appendName(KONTAKTINFORMASJON_DOEDSBO, status);
//                pdlForvalterConsumer.postKontaktinformasjonForDoedsbo(pdldata.getKontaktinformasjonForDoedsbo(), ident);
//                appendOkStatus(status);
//
//            } catch (RuntimeException exception) {
//
//                appendErrorStatus(exception, status);
//            }
//        }
//    }
//
//    private void sendFalskIdentitet(Pdldata pdldata, String ident, StringBuilder status) {
//
//        if (nonNull(pdldata) && nonNull(pdldata.getFalskIdentitet())) {
//            try {
//                appendName(FALSK_IDENTITET, status);
//                pdlForvalterConsumer.postFalskIdentitet(pdldata.getFalskIdentitet(), ident);
//                appendOkStatus(status);
//
//            } catch (RuntimeException exception) {
//
//                appendErrorStatus(exception, status);
//            }
//        }
//    }
//
//    private void sendDeleteIdent(DollyPerson dollyPerson) {
//
//        try {
//            dollyPerson.getPersondetaljer().forEach(person -> pdlForvalterConsumer.deleteIdent(person.getIdent()));
//
//        } catch (WebClientResponseException e) {
//
//            if (!HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
//                log.error(e.getMessage(), e);
//            }
//        } catch (RuntimeException e) {
//
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    private void appendErrorStatus(RuntimeException exception, StringBuilder builder) {
//
//        builder.append('&')
//                .append(errorStatusDecoder.decodeThrowable(exception));
//        log.error(exception.getMessage(), exception);
//    }
//
//    private static void appendName(String utenlandsIdentifikasjonsnummer, StringBuilder builder) {
//        builder.append('$')
//                .append(utenlandsIdentifikasjonsnummer);
//    }
//
//    private static boolean hasNoPdldataAdresse(PersonDTO person) {
//
//        return isNull(person) ||
//                (person.getBostedsadresse().isEmpty() &&
//                        person.getKontaktadresse().isEmpty() &&
//                        person.getOppholdsadresse().isEmpty());
//    }
//
//    private static void appendOkStatus(StringBuilder builder) {
//        builder.append("&OK");
//    }

}
