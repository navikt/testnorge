package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.config.Consumers;
import no.nav.pdl.forvalter.consumer.command.MatrikkeladresseServiceCommand;
import no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand;
import no.nav.testnav.libs.data.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VegadresseDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.remove;

@Slf4j
@Service
public class AdresseServiceConsumer {

    private static final String UOPPGITT = "9999";
    private static final String HISTORISK = "(historisk)";

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final MapperFacade mapperFacade;
    private final KodeverkConsumer kodeverkConsumer;

    public AdresseServiceConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient webClient,
            MapperFacade mapperFacade,
            KodeverkConsumer kodeverkConsumer) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getAdresseService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.mapperFacade = mapperFacade;
        this.kodeverkConsumer = kodeverkConsumer;
    }

    public no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO getVegadresse(VegadresseDTO vegadresse, String matrikkelId) {

        var startTime = currentTimeMillis();
        var vegadresseDTO = mapperFacade.map(vegadresse, VegadresseDTO.class);

        if (UOPPGITT.equals(vegadresse.getKommunenummer())) {
            vegadresseDTO.setKommunenummer(null);
        } else {
            vegadresseDTO.setKommunenummer(sjekkHistorisk(vegadresse.getKommunenummer()));
        }

        return tokenExchange.exchange(serverProperties)
                .flatMap(token ->
                        new VegadresseServiceCommand(webClient, vegadresseDTO, matrikkelId, token.getTokenValue()).call())
                .flatMapMany(Flux::fromArray)
                .next()
                .switchIfEmpty(Mono.defer(() -> Mono.just(VegadresseServiceCommand.defaultAdresse())))
                .doOnNext(adresse -> log.info("Oppslag til adresseservice tok {} ms", currentTimeMillis() - startTime))
                .map(adresse -> {
                    if (isNotBlank(vegadresseDTO.getKommunenummer()) &&
                            isNotBlank(vegadresse.getKommunenummer()) &&
                            !UOPPGITT.equals(vegadresse.getKommunenummer()) &&
                            !"FYRSTIKKALLÉEN".equals(adresse.getAdressenavn())) {
                        adresse.setKommunenummer(vegadresse.getKommunenummer());
                    }
                    return adresse;
                })
                .block();
    }

    public no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO getMatrikkeladresse(MatrikkeladresseDTO adresse, String matrikkelId) {

        var startTime = currentTimeMillis();

        var matrikkeladresseDTO = mapperFacade.map(adresse, MatrikkeladresseDTO.class);

        if (UOPPGITT.equals(matrikkeladresseDTO.getKommunenummer())) {
            matrikkeladresseDTO.setKommunenummer(null);
        } else {
            matrikkeladresseDTO.setKommunenummer(sjekkHistorisk(matrikkeladresseDTO.getKommunenummer()));
        }

        return tokenExchange.exchange(serverProperties)
                .flatMap(token ->
                        new MatrikkeladresseServiceCommand(webClient, adresse, matrikkelId, token.getTokenValue()).call())
                .flatMapMany(Flux::fromArray)
                .next()
                .switchIfEmpty(Mono.defer(() -> Mono.just(MatrikkeladresseServiceCommand.defaultAdresse())))
                .doOnNext(adresseDTO -> log.info("Oppslag til adresseservice tok {} ms", currentTimeMillis() - startTime))
                .map(adresseDTO -> {
                    if (isNotBlank(matrikkeladresseDTO.getKommunenummer()) &&
                            isNotBlank(adresse.getKommunenummer()) &&
                            !UOPPGITT.equals(adresse.getKommunenummer()) &&
                            !"VALEN".equals(adresseDTO.getTilleggsnavn())) {
                        adresseDTO.setKommunenummer(adresse.getKommunenummer());
                    }
                    return adresseDTO;
                })
                .block();
    }

    private String sjekkHistorisk(String kommunenummer) {

        if (isNotBlank(kommunenummer)) {
            var historiske = kodeverkConsumer.getKommunerMedHistoriske();
            var kommunenavn = historiske.get(kommunenummer);
            if (isNotBlank(kommunenavn) && kommunenavn.endsWith(HISTORISK)) {
                var gjeldendeKommunenavn = historiskeKommunerMedNyttNavn(remove(kommunenavn, HISTORISK).trim());
                return historiske.entrySet().stream()
                        .filter(kommune -> kommune.getValue().equals(gjeldendeKommunenavn))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);
            }
        }
        return kommunenummer;
    }

    private String historiskeKommunerMedNyttNavn(String kommunenavn) {

        return switch (kommunenavn) {
            case "Agdenes", "Meldal", "Orkdal", "Snillfjord" -> "Orkland";
            case "Andebu", "Stokke" -> "Sandefjord";
            case "Askim", "Eidsberg", "Fet", "Hobøl", "Spydeberg", "Trøgstad" -> "Indre Østfold";
            case "Audnedal" -> "Lyngdal";
            case "Balestrand", "Leikanger" -> "Sogndal";
            case "Ballangen" -> "Narvik";
            case "Berg", "Lenvik", "Torsken", "Tranøy" -> "Senja";
            case "Bjarkøy" -> "Harstad";
            case "Bjugn" -> "Ørland";
            case "Deatnu Tana" -> "Tana";
            case "Eid", "Selje" -> "Stad";
            case "Eide", "Fræna" -> "Hustadvika";
            case "Finnøy" -> "Stavanger";
            case "Fjell" -> "Øygarden";
            case "Flora" -> "Kinn";
            case "Forsand" -> "Sandnes";
            case "Fosnes", "Namdalseid" -> "Namsos";
            case "Frei" -> "Kristiansund";
            case "Fusa", "Os" -> "Bjørnafjorden";
            case "Førde", "Gaular", "Jølster", "Naustdal" -> "Sunnfjord";
            case "Granvin" -> "Voss";
            case "Guovdageaidnu Kautokeino" -> "Kautokeino";
            case "Gáivuotna Kåfjord" -> "Kåfjord";
            case "Halsa", "Hemne" -> "Heim";
            case "Hof" -> "Åsnes";
            case "Hornindal" -> "Volda";
            case "Hurum", "Røyken" -> "Asker";
            case "Jondal", "Odda" -> "Ullensvang";
            case "Klæbu" -> "Trondheim";
            case "Kvalsund" -> "Hammerfest";
            case "Kárásjohka Karasjok" -> "Karasjok";
            case "Lardal" -> "Larvik";
            case "Leksvik", "Rissa" -> "Indre Fosen";
            case "Lindås", "Meland", "Radøy" -> "Alver";
            case "Mandal", "Marnardal" -> "Lindesnes";
            case "Midsund", "Nesset" -> "Molde";
            case "Mosvik" -> "Inderøy";
            case "Nedre Eiker", "Svelvik" -> "Drammen";
            case "Nes (Ak.)" -> "Nes";
            case "Nes (Busk.)" -> "Nesbyen";
            case "Norddal", "Stordal" -> "Fjord";
            case "Nærøy", "Vikna" -> "Nærøysund";
            case "Nøtterøy", "Tjøme" -> "Færder";
            case "Oppegård", "Ski" -> "Nordre Follo";
            case "Porsanger Porsángu Porsanki" -> "Porsanger";
            case "Re" -> "Tønsberg";
            case "Rennesøy" -> "Stavanger";
            case "Roan" -> "Åfjord";
            case "Rygge" -> "Moss";
            case "Rømskog" -> "Aurskog-Høland";
            case "Røyrvik" -> "Raarvikhe – Røyrvik";
            case "Sandøy", "Skodje", "Ørskog" -> "Ålesund";
            case "Sauherad" -> "Midt-Telemark";
            case "Skedsmo", "Sørum" -> "Lillestrøm";
            case "Skånland" -> "Tjeldsund";
            case "Snåsa" -> "Snåase-Snåsa";
            case "Songdalen", "Søgne" -> "Kristiansand";
            case "Sund" -> "Øygarden";
            case "Tustna" -> "Aure";
            case "Tysfjord" -> "Narvik";
            case "Unjárga Nesseby" -> "Nesseby";
            case "Verran" -> "Steinkjer";
            case "Vågsøy" -> "Kinn";
            case "Våler (Viken)" -> "Våler";
            case "Ølen" -> "Vindafjord";
            default -> kommunenavn;
        };
    }
}
