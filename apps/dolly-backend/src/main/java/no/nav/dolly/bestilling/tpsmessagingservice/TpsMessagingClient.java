package no.nav.dolly.bestilling.tpsmessagingservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdresseUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SpraakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.kontoregisterservice.util.BankkontoGenerator.tilfeldigNorskBankkonto;
import static no.nav.dolly.bestilling.kontoregisterservice.util.BankkontoGenerator.tilfeldigUtlandskBankkonto;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarselSlutt;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsMessagingClient implements ClientRegister {

    private static final String STATUS_FMT = "%s:%s";
    private static final String TPS_MESSAGING = "TPS-meldinger";

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final MapperFacade mapperFacade;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final TransactionHelperService transactionHelperService;
    private final TpsMiljoerConsumer tpsMiljoerConsumer;

    private static String getResultat(TpsMeldingResponseDTO respons) {

        return "OK".equals(respons.getStatus()) ? "OK" : "FEIL= " + respons.getUtfyllendeMelding();
    }

    private static String getStatus(String melding, List<TpsMeldingResponseDTO> statuser) {

        return !statuser.isEmpty() ?

                String.format("%s#%s", melding,
                        statuser.stream()
                                .map(respons -> String.format(STATUS_FMT,
                                        respons.getMiljoe(),
                                        getResultat(respons)))
                                .collect(Collectors.joining(","))) :
                "";
    }

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.from(tpsMiljoerConsumer.getTpsMiljoer()
                        .flatMap(miljoer -> {

                            if (!dollyPerson.isOrdre()) {
                                transactionHelperService.persister(progress, BestillingProgress::setTpsMessagingStatus,
                                        prepTpsMessagingStatus(miljoer, false));
                            }

                            return getIdenterHovedpersonOgPartner(dollyPerson.getIdent())
                                    .flatMap(this::getPersonData)
                                    .collectList()
                                    .map(personer -> tpsMessagingConsumer.getToken()
                                            .flatMapMany(token -> Flux.concat(
                                                    sendSpraakkode(bestilling, dollyPerson.getIdent(), token)
                                                            .map(respons -> Map.of("SpråkKode", respons)),
                                                    sendBankkontonummerNorge(bestilling, dollyPerson.getIdent(), token)
                                                            .map(respons -> Map.of("NorskBankkonto", respons)),
                                                    sendBankkontonummerUtenland(bestilling, dollyPerson.getIdent(), token)
                                                            .map(respons -> Map.of("UtenlandskBankkonto", respons)),
                                                    sendEgenansattSlett(bestilling, dollyPerson.getIdent(), token)
                                                            .map(respons -> Map.of("Egenansatt_slett", respons)),
                                                    sendEgenansatt(bestilling, dollyPerson.getIdent(), token)
                                                            .map(respons -> Map.of("Egenansatt_opprett", respons)),
                                                    sendSikkerhetstiltakSlett(personer.stream()
                                                            .filter(personBolk -> personBolk.getIdent().equals(dollyPerson.getIdent()))
                                                            .findFirst(), token)
                                                            .map(respons -> Map.of("Sikkerhetstiltak_slett", respons)),
                                                    sendSikkerhetstiltakOpprett(personer.stream()
                                                            .filter(personBolk -> personBolk.getIdent().equals(dollyPerson.getIdent()))
                                                            .findFirst(), token)
                                                            .map(respons -> Map.of("Sikkerhetstiltak_opprett", respons)),
                                                    sendTelefonnumreSlett(personer.stream()
                                                            .filter(personBolk -> personBolk.getIdent().equals(dollyPerson.getIdent()))
                                                            .findFirst(), token)
                                                            .map(respons -> Map.of("Telefonnummer_slett", respons)),
                                                    sendTelefonnumreOpprett(personer.stream()
                                                            .filter(personBolk -> personBolk.getIdent().equals(dollyPerson.getIdent()))
                                                            .findFirst(), token)
                                                            .map(respons -> Map.of("Telefonnummer_opprett", respons)),
                                                    sendBostedsadresseUtland(personer, token)
                                                            .map(respons -> Map.of("BostedadresseUtland", respons)),
                                                    sendKontaktadresseUtland(personer, token)
                                                            .map(respons -> Map.of("KontaktadresseUtland", respons))
                                            ))
                                            .map(respons -> respons.entrySet().stream()
                                                    .map(entry -> getStatus(entry.getKey(), entry.getValue()))
                                                    .toList())
                                            .flatMap(Flux::fromIterable)
                                            .filter(StringUtils::isNotBlank)
                                            .collect(Collectors.joining("$")))
                                    .flatMap(Mono::from);
                        }))
                .map(status -> futurePersist(dollyPerson, progress, status));
    }

    private ClientFuture futurePersist(DollyPerson dollyPerson, BestillingProgress progress, String status) {

        return () -> {
            if (!dollyPerson.isOrdre()) {
                transactionHelperService.persister(progress, BestillingProgress::setTpsMessagingStatus, status);
            }
            return progress;
        };
    }

    private String prepTpsMessagingStatus(List<String> miljoer, boolean isFinal) {

        return miljoer.stream()
                .map(miljo -> String.format("%s:%s", miljo, encodeStatus(isFinal ?
                        getVarselSlutt(TPS_MESSAGING) : getInfoVenter(TPS_MESSAGING))))
                .collect(Collectors.joining(","));
    }

    @Override
    public void release(List<String> identer) {

        // TpsMessaging har ikke sletting
    }

    private Flux<List<String>> getIdenterHovedpersonOgPartner(String ident) {

        return getPersonData(List.of(ident))
                .map(person -> Stream.of(List.of(ident),
                                person.getPerson().getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull)
                                        .toList())
                        .flatMap(Collection::stream)
                        .distinct()
                        .toList());
    }

    private Flux<PdlPersonBolk.PersonBolk> getPersonData(List<String> identer) {

        return pdlPersonConsumer.getPdlPersoner(identer)
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()));
    }

    private Mono<List<TpsMeldingResponseDTO>> sendBostedsadresseUtland(List<PdlPersonBolk.PersonBolk> pdlPersoner, AccessToken token) {

        var bostedadresseResponse = pdlPersoner.stream()
                .filter(person -> !person.getPerson().getBostedsadresse().isEmpty() &&
                        person.getPerson().getBostedsadresse().get(0).isAdresseUtland())
                .map(person ->
                        tpsMessagingConsumer.sendAdresseUtlandRequest(person.getIdent(), null,
                                        mapperFacade.map(person.getPerson().getBostedsadresse().get(0), AdresseUtlandDTO.class), token)
                                .collectList())
                .toList();

        return !bostedadresseResponse.isEmpty() ? bostedadresseResponse.get(0) : Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendKontaktadresseUtland(List<PdlPersonBolk.PersonBolk> pdlPersoner, AccessToken token) {

        var kontaktadresseResponse = pdlPersoner.stream()
                .filter(person -> !person.getPerson().getKontaktadresse().isEmpty() &&
                        person.getPerson().getKontaktadresse().get(0).isAdresseUtland())
                .map(person ->
                        tpsMessagingConsumer.sendAdresseUtlandRequest(person.getIdent(), null,
                                        mapperFacade.map(person.getPerson().getKontaktadresse().get(0), AdresseUtlandDTO.class), token)
                                .collectList())
                .toList();

        return !kontaktadresseResponse.isEmpty() ? kontaktadresseResponse.get(0) : Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendTelefonnumreSlett(Optional<PdlPersonBolk.PersonBolk> personBolk, AccessToken token) {

        return personBolk.isPresent() && !personBolk.get().getPerson().getTelefonnummer().isEmpty() ?

                tpsMessagingConsumer.deleteTelefonnummerRequest(
                                personBolk.get().getIdent(), null, token)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendTelefonnumreOpprett(Optional<PdlPersonBolk.PersonBolk> personBolk, AccessToken token) {

        return personBolk.isPresent() && !personBolk.get().getPerson().getTelefonnummer().isEmpty() ?

                tpsMessagingConsumer.sendTelefonnummerRequest(
                                personBolk.get().getIdent(),
                                null,
                                mapperFacade.mapAsList(personBolk.get().getPerson().getTelefonnummer(),
                                        TelefonTypeNummerDTO.class), token)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendSikkerhetstiltakSlett(Optional<PdlPersonBolk.PersonBolk> personBolk, AccessToken token) {

        return personBolk.isPresent() && !personBolk.get().getPerson().getSikkerhetstiltak().isEmpty() ?

                tpsMessagingConsumer.deleteSikkerhetstiltakRequest(
                                personBolk.get().getIdent(), null, token)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendSikkerhetstiltakOpprett(Optional<PdlPersonBolk.PersonBolk> personBolk, AccessToken token) {

        return personBolk.isPresent() && !personBolk.get().getPerson().getSikkerhetstiltak().isEmpty() ?

                tpsMessagingConsumer.sendSikkerhetstiltakRequest(
                                personBolk.get().getIdent(), null,
                                personBolk.get().getPerson().getSikkerhetstiltak()
                                        .stream().findFirst().orElse(new SikkerhetstiltakDTO()), token)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendSpraakkode(RsDollyUtvidetBestilling bestilling,
                                                             String ident, AccessToken token) {

        return nonNull(bestilling.getTpsMessaging()) && nonNull(bestilling.getTpsMessaging().getSpraakKode()) ?

                tpsMessagingConsumer.sendSpraakkodeRequest(ident, null,
                                mapperFacade.map(bestilling.getTpsMessaging().getSpraakKode(), SpraakDTO.class), token)
                        .collectList() :

                Mono.just(emptyList());
    }


    private Mono<List<TpsMeldingResponseDTO>> sendEgenansattSlett(RsDollyUtvidetBestilling bestilling,
                                                                  String ident, AccessToken token) {

        return nonNull(SkjermingUtil.getEgenansattDatoTom(bestilling)) ?

                tpsMessagingConsumer.deleteEgenansattRequest(ident, null, token)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendEgenansatt(RsDollyUtvidetBestilling bestilling,
                                                             String ident, AccessToken token) {

        return nonNull(SkjermingUtil.getEgenansattDatoFom(bestilling)) ?

                tpsMessagingConsumer.sendEgenansattRequest(ident, null,
                                SkjermingUtil.getEgenansattDatoFom(bestilling).toLocalDate(), token)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendBankkontonummerNorge(RsDollyUtvidetBestilling bestilling,
                                                                       String ident, AccessToken token) {

        if (nonNull(bestilling.getBankkonto()) && nonNull(bestilling.getBankkonto().getNorskBankkonto())) {

            if (isTrue(bestilling.getBankkonto().getNorskBankkonto().getTilfeldigKontonummer())) {
                bestilling.getBankkonto().getNorskBankkonto().setKontonummer(tilfeldigNorskBankkonto());
            }

            return tpsMessagingConsumer.sendNorskBankkontoRequest(
                            ident, null, bestilling.getBankkonto().getNorskBankkonto(), token)
                    .collectList();

        } else {
            return Mono.just(emptyList());
        }
    }

    private Mono<List<TpsMeldingResponseDTO>> sendBankkontonummerUtenland(RsDollyUtvidetBestilling bestilling,
                                                                          String ident, AccessToken token) {

        if (nonNull(bestilling.getBankkonto()) && nonNull(bestilling.getBankkonto().getUtenlandskBankkonto())) {

            if (isTrue(bestilling.getBankkonto().getUtenlandskBankkonto().getTilfeldigKontonummer())) {
                bestilling.getBankkonto().getUtenlandskBankkonto()
                        .setKontonummer(tilfeldigUtlandskBankkonto(
                                bestilling.getBankkonto().getUtenlandskBankkonto().getLandkode()));
            }

            return tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                            ident, null, bestilling.getBankkonto().getUtenlandskBankkonto(), token)
                    .collectList();

        } else {

            return Mono.just(emptyList());
        }
    }
}
