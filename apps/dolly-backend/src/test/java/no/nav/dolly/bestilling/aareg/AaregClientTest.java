package no.nav.dolly.bestilling.aareg;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.OrdinaerArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.Person;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Map;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AaregClientTest {

    private static final String IDENT = "111111111111";
    private static final String ENV = "u2";
    private static final String ORGNUMMER = "222222222";

    @Mock
    private AaregConsumer aaregConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private AccessToken accessToken;

    @Mock
    private TransactionHelperService transactionHelperService;

    @InjectMocks
    private AaregClient aaregClient;

    @BeforeEach
    void setup() {
        when(aaregConsumer.getAccessToken()).thenReturn(Mono.just(accessToken));

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        new JwtAuthenticationToken(
                                new Jwt(
                                        "token",
                                        Instant.now(),
                                        Instant.now().plusSeconds(1000),
                                        Map.of("alg", "none"),
                                        Map.of("sub", "n/a")
                                )
                        )
                );
    }

    private ArbeidsforholdRespons buildArbeidsforhold(boolean isOrgnummer) {

        return ArbeidsforholdRespons.builder()
                .eksisterendeArbeidsforhold(singletonList(
                        Arbeidsforhold.builder()
                                .arbeidstaker(Person.builder()
                                        .offentligIdent(IDENT)
                                        .build())
                                .arbeidsgiver(isOrgnummer ?
                                        Organisasjon.builder()
                                                .organisasjonsnummer(ORGNUMMER)
                                                .build() :
                                        Person.builder()
                                                .offentligIdent(IDENT)
                                                .build())
                                .arbeidsavtaler(singletonList(OrdinaerArbeidsavtale.builder()
                                        .yrke("121232")
                                        .arbeidstidsordning("nada")
                                        .build()))
                                .type("ordinaert")
                                .arbeidsforholdId("1")
                                .isOppdatering(true)
                                .build()))
                .build();
    }

    @Test
    void gjenopprettArbeidsforhold_intetTidligereArbeidsforholdFinnes_OK() {
        when(mapperFacade.mapAsList(anyList(), eq(Arbeidsforhold.class), any())).thenReturn(singletonList(new Arbeidsforhold()));
        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV, accessToken)).thenReturn(Mono.just(new ArbeidsforholdRespons()));
        when(aaregConsumer.opprettArbeidsforhold(any(Arbeidsforhold.class), eq(ENV), eq(accessToken)))
                .thenReturn(Flux.just(new ArbeidsforholdRespons()));
        when(mapperFacade.mapAsList(anyList(), eq(Arbeidsforhold.class)))
                .thenReturn(buildArbeidsforhold(true).getEksisterendeArbeidsforhold());

        var request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsAareg.builder().build()));
        request.setEnvironments(singleton(ENV));
        aaregClient.gjenopprett(request,
                        DollyPerson.builder().hovedperson(IDENT)
                                .build(), new BestillingProgress(), false)
                .subscribe(resultat ->
                        verify(aaregConsumer).opprettArbeidsforhold(any(Arbeidsforhold.class), eq(ENV), eq(accessToken)));
    }

    @Test
    void gjenopprettArbeidsforhold_intetTidligereArbeidsforholdFinnes_lesKasterException() {
        when(mapperFacade.mapAsList(anyList(), eq(Arbeidsforhold.class), any())).thenReturn(singletonList(new Arbeidsforhold()));
        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV, accessToken)).thenReturn(Mono.just(new ArbeidsforholdRespons()));
        when(aaregConsumer.opprettArbeidsforhold(any(Arbeidsforhold.class), eq(ENV), eq(accessToken)))
                .thenReturn(Flux.just(new ArbeidsforholdRespons()));
        when(mapperFacade.mapAsList(anyList(), eq(Arbeidsforhold.class)))
                .thenReturn(buildArbeidsforhold(true).getEksisterendeArbeidsforhold());

        var request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsAareg.builder().build()));
        request.setEnvironments(singleton(ENV));
        aaregClient.gjenopprett(request,
                        DollyPerson.builder().hovedperson(IDENT)
                               .build(), new BestillingProgress(), false)
                .subscribe(resultat ->
                        verify(aaregConsumer).opprettArbeidsforhold(any(Arbeidsforhold.class), eq(ENV), eq(accessToken)));
    }

    @Test
    void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnesAktoerPerson_returnsOK() {
        var request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsAareg.builder()
                .arbeidsgiver(RsAktoerPerson.builder().ident(IDENT).build())
                .arbeidsforholdId("1")
                .build()));
        request.setEnvironments(singleton(ENV));

        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV, accessToken)).thenReturn(Mono.just(
                buildArbeidsforhold(false)));
        when(mapperFacade.mapAsList(anyList(), eq(Arbeidsforhold.class), any(MappingContext.class)))
                .thenReturn(buildArbeidsforhold(false)
                        .getEksisterendeArbeidsforhold());
        when(aaregConsumer.endreArbeidsforhold(any(Arbeidsforhold.class), eq(ENV), eq(accessToken)))
                .thenReturn(Flux.just(ArbeidsforholdRespons.builder()
                        .miljo(ENV)
                        .arbeidsforholdId("1")
                        .build()));
        when(mapperFacade.mapAsList(anyList(), eq(Arbeidsforhold.class)))
                .thenReturn(buildArbeidsforhold(false).getEksisterendeArbeidsforhold());

        var progress = new BestillingProgress();

        StepVerifier.create(aaregClient.gjenopprett(request,
                                DollyPerson.builder().hovedperson(IDENT)
                                        .build(), progress, false)
                        .map(ClientFuture::get))
                .expectNext(BestillingProgress.builder()
                        .aaregStatus("u2: arbforhold=1$OK")
                        .build())
                .verifyComplete();
    }

    @Test
    void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnesAktoerOrganisasjon_returnsOK() {
        var request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsAareg.builder()
                .arbeidsgiver(RsOrganisasjon.builder().orgnummer(ORGNUMMER).build())
                .arbeidsforholdId("1")
                .isOppdatering(true)
                .build()));
        request.setEnvironments(singleton(ENV));

        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV, accessToken))
                .thenReturn(Mono.just(buildArbeidsforhold(true)));
        when(mapperFacade.mapAsList(anyList(), eq(Arbeidsforhold.class), any(MappingContext.class)))
                .thenReturn(buildArbeidsforhold(true)
                        .getEksisterendeArbeidsforhold());
        when(aaregConsumer.endreArbeidsforhold(any(Arbeidsforhold.class), eq(ENV), eq(accessToken)))
                .thenReturn(Flux.just(ArbeidsforholdRespons.builder()
                        .miljo(ENV)
                        .arbeidsforholdId("1")
                        .build()));
        when(mapperFacade.mapAsList(anyList(), eq(Arbeidsforhold.class)))
                .thenReturn(buildArbeidsforhold(true).getEksisterendeArbeidsforhold());

        var progress = new BestillingProgress();

        StepVerifier.create(aaregClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT)
                                .build(), progress, false)
                        .map(ClientFuture::get))
                .expectNext(BestillingProgress.builder()
                        .aaregStatus("u2: arbforhold=1$OK")
                        .build())
                .verifyComplete();
    }

}