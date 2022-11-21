package no.nav.dolly.bestilling.tpsmessagingservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdresseUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SpraakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.kontoregisterservice.util.BankkontoGenerator.tilfeldigNorskBankkonto;
import static no.nav.dolly.bestilling.kontoregisterservice.util.BankkontoGenerator.tilfeldigUtlandskBankkonto;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@Order(3)
@RequiredArgsConstructor
public class TpsMessagingClient implements ClientRegister {

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final MapperFacade mapperFacade;
    private final DollyPersonCache dollyPersonCache;

    private static String getResultat(TpsMeldingResponseDTO respons) {

        return "OK".equals(respons.getStatus()) ? "OK" : "FEIL= " + respons.getUtfyllendeMelding();
    }

    private static String getStatus(String melding, List<TpsMeldingResponseDTO> statuser) {

        return !statuser.isEmpty() ?

                String.format("%s#%s", melding,
                        statuser.stream()
                                .map(respons -> String.format("%s:%s",
                                        respons.getMiljoe(),
                                        getResultat(respons)))
                                .collect(Collectors.joining(","))) :
                "";
    }

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        dollyPersonCache.fetchIfEmpty(dollyPerson);

        progress.setTpsMessagingStatus(tpsMessagingConsumer.getToken()
                .flatMapMany(token -> Flux.concat(
                        sendSpraakkode(bestilling, dollyPerson.getHovedperson(), token)
                                .map(respons -> Map.of("SpråkKode", respons)),
                        sendBankkontonummerNorge(bestilling, dollyPerson.getHovedperson(), token)
                                .map(respons -> Map.of("NorskBankkonto", respons)),
                        sendBankkontonummerUtenland(bestilling, dollyPerson.getHovedperson(), token)
                                .map(respons -> Map.of("UtenlandskBankkonto", respons)),
                        sendEgenansattSlett(bestilling, dollyPerson.getHovedperson(), token)
                                .map(respons -> Map.of("Egenansatt_slett", respons)),
                        sendEgenansatt(bestilling, dollyPerson.getHovedperson(), token)
                                .map(respons -> Map.of("Egenansatt_opprett", respons)),
                        sendSikkerhetstiltakSlett(dollyPerson, token)
                                .map(respons -> Map.of("Sikkerhetstiltak_slett", respons)),
                        sendSikkerhetstiltakOpprett(dollyPerson, token)
                                .map(respons -> Map.of("Sikkerhetstiltak_opprett", respons)),
                        sendTelefonnumreSlett(dollyPerson, token)
                                .map(respons -> Map.of("Telefonnummer_slett", respons)),
                        sendTelefonnumreOpprett(dollyPerson, token)
                                .map(respons -> Map.of("Telefonnummer_opprett", respons)),
                        sendBostedsadresseUtland(dollyPerson, token)
                                .map(respons -> Map.of("BostedadresseUtland", respons)),
                        sendBostedsadresseUtlandPartner(dollyPerson, token)
                                .map(respons -> Map.of("BostedadresseUtlandPartner", respons)),
                        sendKontaktadresseUtland(dollyPerson, token)
                                .map(respons -> Map.of("KontaktadresseUtland", respons))
                ))
                .map(respons -> respons.entrySet().stream()
                        .map(entry -> getStatus(entry.getKey(), entry.getValue()))
                        .toList())
                .flatMap(Flux::fromIterable)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("$"))
                .block());

        return Flux.just();
    }

    @Override
    public void release(List<String> identer) {

        // TpsMessaging har ikke sletting
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getTpsMessaging()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getTpsMessagingStatus()));
    }

    private Mono<List<TpsMeldingResponseDTO>> sendBostedsadresseUtland(DollyPerson dollyPerson, AccessToken token) {

        return nonNull(dollyPerson.getPdlfPerson()) && dollyPerson.getPdlfPerson().getPerson().getBostedsadresse().stream()
                .anyMatch(BostedadresseDTO::isAdresseUtland) ?

                tpsMessagingConsumer.sendAdresseUtlandRequest(dollyPerson.getHovedperson(), null,
                                mapperFacade.map(dollyPerson.getPdlfPerson().getPerson().getBostedsadresse().stream()
                                        .filter(AdresseDTO::isAdresseUtland)
                                        .findFirst().orElse(new BostedadresseDTO()), AdresseUtlandDTO.class), token)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendBostedsadresseUtlandPartner(DollyPerson dollyPerson, AccessToken token) {

        return nonNull(dollyPerson.getPdlfPerson()) && dollyPerson.getPdlfPerson().getRelasjoner().stream()
                .filter(relasjon -> relasjon.getRelasjonType() == RelasjonType.EKTEFELLE_PARTNER)
                .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                .map(PersonDTO::getBostedsadresse)
                .flatMap(Collection::stream)
                .anyMatch(AdresseDTO::isAdresseUtland) ?

                tpsMessagingConsumer.sendAdresseUtlandRequest(dollyPerson.getPdlfPerson().getRelasjoner().stream()
                                        .filter(relasjon -> relasjon.getRelasjonType() == RelasjonType.EKTEFELLE_PARTNER)
                                        .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                        .map(PersonDTO::getIdent)
                                        .findFirst().orElse("00000000000"), null,
                                mapperFacade.map(dollyPerson.getPdlfPerson().getRelasjoner().stream()
                                        .filter(relasjon -> relasjon.getRelasjonType() == RelasjonType.EKTEFELLE_PARTNER)
                                        .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                        .map(PersonDTO::getBostedsadresse)
                                        .flatMap(Collection::stream)
                                        .filter(AdresseDTO::isAdresseUtland)
                                        .findFirst().orElse(new BostedadresseDTO()), AdresseUtlandDTO.class), token)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendKontaktadresseUtland(DollyPerson dollyPerson, AccessToken token) {

        return nonNull(dollyPerson.getPdlfPerson()) && dollyPerson.getPdlfPerson().getPerson().getKontaktadresse().stream()
                .anyMatch(KontaktadresseDTO::isAdresseUtland) ?

                tpsMessagingConsumer.sendAdresseUtlandRequest(dollyPerson.getHovedperson(), null,
                                mapperFacade.map(dollyPerson.getPdlfPerson().getPerson().getKontaktadresse().stream()
                                        .filter(AdresseDTO::isAdresseUtland)
                                        .findFirst().orElse(new KontaktadresseDTO()), AdresseUtlandDTO.class), token)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendTelefonnumreSlett(DollyPerson dollyPerson, AccessToken token) {

        return nonNull(dollyPerson.getPdlfPerson()) && !dollyPerson.getPdlfPerson().getPerson().getTelefonnummer().isEmpty() ?

                tpsMessagingConsumer.deleteTelefonnummerRequest(
                                dollyPerson.getHovedperson(), null, token)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendTelefonnumreOpprett(DollyPerson dollyPerson, AccessToken token) {

        return nonNull(dollyPerson.getPdlfPerson()) && !dollyPerson.getPdlfPerson().getPerson().getTelefonnummer().isEmpty() ?

                tpsMessagingConsumer.sendTelefonnummerRequest(
                                dollyPerson.getHovedperson(),
                                null,
                                mapperFacade.mapAsList(dollyPerson.getPdlfPerson().getPerson().getTelefonnummer(),
                                        TelefonTypeNummerDTO.class), token)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendSikkerhetstiltakSlett(DollyPerson dollyPerson, AccessToken token) {

        return nonNull(dollyPerson.getPdlfPerson()) && !dollyPerson.getPdlfPerson().getPerson().getSikkerhetstiltak().isEmpty() ?

                tpsMessagingConsumer.deleteSikkerhetstiltakRequest(
                                dollyPerson.getHovedperson(), null, token)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendSikkerhetstiltakOpprett(DollyPerson dollyPerson, AccessToken token) {

        return nonNull(dollyPerson.getPdlfPerson()) && !dollyPerson.getPdlfPerson().getPerson().getSikkerhetstiltak().isEmpty() ?

                tpsMessagingConsumer.sendSikkerhetstiltakRequest(
                                dollyPerson.getHovedperson(), null,
                                dollyPerson.getPdlfPerson().getPerson().getSikkerhetstiltak()
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
