package no.nav.dolly.bestilling.tpsmessagingservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.SpraakDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.TpsMeldingResponseDTO;
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
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsMessagingClient implements ClientRegister {

    private static final String STATUS_FMT = "%s:%s";
    private static final String TPS_MESSAGING = "TPS";

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final MapperFacade mapperFacade;
    private final PersonServiceConsumer personServiceConsumer;
    private final TransactionHelperService transactionHelperService;
    private final TpsMiljoerConsumer tpsMiljoerConsumer;

    private static String getResultat(TpsMeldingResponseDTO respons) {

        return "OK".equals(respons.getStatus()) ? "OK" :
                ErrorStatusDecoder.encodeStatus("FEIL: " + respons.getUtfyllendeMelding());
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
    @SuppressWarnings("S1144")
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.from(tpsMiljoerConsumer.getTpsMiljoer()
                        .flatMap(miljoer -> {

                            if (!dollyPerson.isOrdre() && isTpsMessage(bestilling)) {
                                transactionHelperService.persister(progress, BestillingProgress::getTpsMessagingStatus,
                                        BestillingProgress::setTpsMessagingStatus,
                                        prepTpsMessagingStatus(miljoer));
                            }

                            return getIdenterHovedpersonOgPartner(dollyPerson.getIdent())
                                    .flatMap(this::getPersonData)
                                    .collectList()
                                    .flatMapMany(personer -> Flux.concat(
                                            sendSpraakkode(bestilling, dollyPerson.getIdent())
                                                    .map(respons -> Map.of("SpråkKode", respons)),
                                            sendBankkontonummerNorge(bestilling, dollyPerson.getIdent())
                                                    .map(respons -> Map.of("NorskBankkonto", respons)),
                                            sendBankkontonummerUtenland(bestilling, dollyPerson.getIdent())
                                                    .map(respons -> Map.of("UtenlandskBankkonto", respons)),
                                            sendEgenansattSlett(bestilling, dollyPerson.getIdent())
                                                    .map(respons -> Map.of("Egenansatt_slett", respons)),
                                            sendEgenansatt(bestilling, dollyPerson.getIdent())
                                                    .map(respons -> Map.of("Egenansatt_opprett", respons)),
                                            sendSikkerhetstiltakSlett(personer.stream()
                                                    .filter(personBolk -> personBolk.getIdent().equals(dollyPerson.getIdent()))
                                                    .findFirst())
                                                    .map(respons -> Map.of("Sikkerhetstiltak_slett", respons)),
                                            sendSikkerhetstiltakOpprett(personer.stream()
                                                    .filter(personBolk -> personBolk.getIdent().equals(dollyPerson.getIdent()))
                                                    .findFirst())
                                                    .map(respons -> Map.of("Sikkerhetstiltak_opprett", respons))
                                    ))
                                    .map(respons -> respons.entrySet().stream()
                                            .map(entry -> getStatus(entry.getKey(), entry.getValue()))
                                            .toList())
                                    .flatMap(Flux::fromIterable)
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.joining("$"));
                        }))
                .map(status -> futurePersist(dollyPerson, progress, status));
    }

    private boolean isTpsMessage(RsDollyUtvidetBestilling bestilling) {

        return isNotBlank(bestilling.getTpsMessaging().getSpraakKode()) ||
                nonNull(bestilling.getBankkonto()) ||
                nonNull(bestilling.getSkjerming()) ||
                (nonNull(bestilling.getPdldata()) &&
                        nonNull(bestilling.getPdldata().getPerson()) &&
                        !bestilling.getPdldata().getPerson().getSikkerhetstiltak().isEmpty());
    }

    private ClientFuture futurePersist(DollyPerson dollyPerson, BestillingProgress progress, String status) {

        return () -> {
            if (!dollyPerson.isOrdre()) {
                transactionHelperService.persister(progress, BestillingProgress::getTpsMessagingStatus,
                        BestillingProgress::setTpsMessagingStatus, status);
            }
            return progress;
        };
    }

    private String prepTpsMessagingStatus(List<String> miljoer) {

        return "Startet#" + miljoer.stream()
                .map(miljo -> String.format(STATUS_FMT, miljo, getInfoVenter(TPS_MESSAGING)))
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

        return personServiceConsumer.getPdlPersoner(identer)
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()));
    }

    private Mono<List<TpsMeldingResponseDTO>> sendSikkerhetstiltakSlett(Optional<PdlPersonBolk.PersonBolk> personBolk) {

        return personBolk.isPresent() && !personBolk.get().getPerson().getSikkerhetstiltak().isEmpty() ?

                tpsMessagingConsumer.deleteSikkerhetstiltakRequest(
                                personBolk.get().getIdent(), null)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendSikkerhetstiltakOpprett(Optional<PdlPersonBolk.PersonBolk> personBolk) {

        return personBolk.isPresent() && !personBolk.get().getPerson().getSikkerhetstiltak().isEmpty() ?

                tpsMessagingConsumer.sendSikkerhetstiltakRequest(
                                personBolk.get().getIdent(), null,
                                personBolk.get().getPerson().getSikkerhetstiltak()
                                        .stream().findFirst().orElse(new SikkerhetstiltakDTO()))
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendSpraakkode(RsDollyUtvidetBestilling bestilling, String ident) {

        return nonNull(bestilling.getTpsMessaging()) && nonNull(bestilling.getTpsMessaging().getSpraakKode()) ?

                tpsMessagingConsumer.sendSpraakkodeRequest(ident, null,
                                mapperFacade.map(bestilling.getTpsMessaging().getSpraakKode(), SpraakDTO.class))
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendEgenansattSlett(RsDollyUtvidetBestilling bestilling,
                                                                  String ident) {

        return nonNull(SkjermingUtil.getEgenansattDatoTom(bestilling)) ?

                tpsMessagingConsumer.deleteEgenansattRequest(ident, null)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendEgenansatt(RsDollyUtvidetBestilling bestilling,
                                                             String ident) {

        return nonNull(SkjermingUtil.getEgenansattDatoFom(bestilling)) ?

                tpsMessagingConsumer.sendEgenansattRequest(ident, null,
                                SkjermingUtil.getEgenansattDatoFom(bestilling).toLocalDate())
                        .collectList() :

                Mono.just(emptyList());
    }

    @SuppressWarnings("S1144")
    private Mono<List<TpsMeldingResponseDTO>> sendBankkontonummerNorge(RsDollyUtvidetBestilling bestilling,
                                                                       String ident) {

        if (nonNull(bestilling.getBankkonto()) && nonNull(bestilling.getBankkonto().getNorskBankkonto())) {

            if (isTrue(bestilling.getBankkonto().getNorskBankkonto().getTilfeldigKontonummer())) {
                bestilling.getBankkonto().getNorskBankkonto().setKontonummer(tilfeldigNorskBankkonto());
            }

            return tpsMessagingConsumer.sendNorskBankkontoRequest(
                            ident, null, bestilling.getBankkonto().getNorskBankkonto())
                    .collectList();

        } else {
            return Mono.just(emptyList());
        }
    }

    @SuppressWarnings("S1144")
    private Mono<List<TpsMeldingResponseDTO>> sendBankkontonummerUtenland(RsDollyUtvidetBestilling bestilling,
                                                                          String ident) {

        if (nonNull(bestilling.getBankkonto()) && nonNull(bestilling.getBankkonto().getUtenlandskBankkonto())) {

            if (isTrue(bestilling.getBankkonto().getUtenlandskBankkonto().getTilfeldigKontonummer())) {
                bestilling.getBankkonto().getUtenlandskBankkonto()
                        .setKontonummer(tilfeldigUtlandskBankkonto(
                                bestilling.getBankkonto().getUtenlandskBankkonto().getLandkode()));
            }

            return tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                            ident, null, bestilling.getBankkonto().getUtenlandskBankkonto())
                    .collectList();

        } else {

            return Mono.just(emptyList());
        }
    }
}
