package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.aareg.AaregConsumer;
import no.nav.dolly.bestilling.aareg.ArbeidsforholdServiceConsumer;
import no.nav.dolly.bestilling.aareg.amelding.AmeldingConsumer;
import no.nav.dolly.bestilling.aareg.amelding.OrganisasjonServiceConsumer;
import no.nav.dolly.bestilling.arenaforvalter.ArenaForvalterConsumer;
import no.nav.dolly.bestilling.brregstub.BrregstubConsumer;
import no.nav.dolly.bestilling.dokarkiv.DokarkivConsumer;
import no.nav.dolly.bestilling.inntektsmelding.InntektsmeldingConsumer;
import no.nav.dolly.bestilling.inntektstub.InntektstubConsumer;
import no.nav.dolly.bestilling.instdata.InstdataConsumer;
import no.nav.dolly.bestilling.krrstub.KrrstubConsumer;
import no.nav.dolly.bestilling.organisasjonforvalter.OrganisasjonConsumer;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdlforvalter.PdlForvalterConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.sigrunstub.SigrunStubConsumer;
import no.nav.dolly.bestilling.skjermingsregister.SkjermingsRegisterConsumer;
import no.nav.dolly.bestilling.sykemelding.HelsepersonellConsumer;
import no.nav.dolly.bestilling.sykemelding.SykemeldingConsumer;
import no.nav.dolly.bestilling.udistub.UdiStubConsumer;
import no.nav.dolly.consumer.fastedatasett.FasteDatasettConsumer;
import no.nav.dolly.consumer.generernavn.GenererNavnConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/config", produces = MediaType.APPLICATION_JSON_VALUE)
public class EnvironmentIsAliveController {

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final AaregConsumer aaregConsumer;
    private final AmeldingConsumer ameldingConsumer;
    private final ArbeidsforholdServiceConsumer arbeidsforholdServiceConsumer;
    private final BrregstubConsumer brregstubConsumer;
    private final DokarkivConsumer dokarkivConsumer;
    private final FasteDatasettConsumer fasteDatasettConsumer;
    private final GenererNavnConsumer genererNavnConsumer;
    private final HelsepersonellConsumer helsepersonellConsumer;
    private final InntektsmeldingConsumer inntektsmeldingConsumer;
    private final InntektstubConsumer inntektstubConsumer;
    private final InstdataConsumer instdataConsumer;
    private final KodeverkConsumer kodeverkConsumer;
    private final KrrstubConsumer krrstubConsumer;
    private final OrganisasjonServiceConsumer organisasjonServiceConsumer;
    private final OrganisasjonConsumer organisasjonConsumer;
    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final PersonServiceConsumer personServiceConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final PdlDataConsumer pdlDataConsumer;
    private final PdlForvalterConsumer pdlForvalterConsumer;
    private final SykemeldingConsumer sykemeldingConsumer;
    private final SkjermingsRegisterConsumer skjermingsRegisterConsumer;
    private final SigrunStubConsumer sigrunStubConsumer;
    private final UdiStubConsumer udiStubConsumer;

    @GetMapping("/isAlive")
    @Operation(description = "Sjekk om applikasjonene er i live")
    public Map<String, String> checkAlive() {

        return Stream.of(
                        arenaForvalterConsumer.checkAlive().entrySet(),
                        aaregConsumer.checkAlive().entrySet(),
                        ameldingConsumer.checkAlive().entrySet(),
                        arbeidsforholdServiceConsumer.checkAlive().entrySet(),
                        brregstubConsumer.checkAlive().entrySet(),
                        dokarkivConsumer.checkAlive().entrySet(),
                        fasteDatasettConsumer.checkAlive().entrySet(),
                        genererNavnConsumer.checkAlive().entrySet(),
                        helsepersonellConsumer.checkAlive().entrySet(),
                        inntektsmeldingConsumer.checkAlive().entrySet(),
                        inntektstubConsumer.checkAlive().entrySet(),
                        instdataConsumer.checkAlive().entrySet(),
                        kodeverkConsumer.checkAlive().entrySet(),
                        krrstubConsumer.checkAlive().entrySet(),
                        organisasjonConsumer.checkAlive().entrySet(),
                        organisasjonServiceConsumer.checkAlive().entrySet(),
                        pdlDataConsumer.checkAlive().entrySet(),
                        personServiceConsumer.checkAlive().entrySet(),
                        pdlForvalterConsumer.checkAlive().entrySet(),
                        pdlPersonConsumer.checkAlive().entrySet(),
                        pensjonforvalterConsumer.checkAlive().entrySet(),
                        sigrunStubConsumer.checkAlive().entrySet(),
                        skjermingsRegisterConsumer.checkAlive().entrySet(),
                        sykemeldingConsumer.checkAlive().entrySet(),
                        udiStubConsumer.checkAlive().entrySet()
                )
                .flatMap(Set::stream)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
