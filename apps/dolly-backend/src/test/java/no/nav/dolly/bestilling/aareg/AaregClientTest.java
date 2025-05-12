package no.nav.dolly.bestilling.aareg;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.OrdinaerArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.Person;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AaregClientTest {

    private static final String IDENT = "111111111111";
    private static final String ENV = "q2";
    private static final String ORGNUMMER = "222222222";

    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private AaregConsumer aaregConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private AccessToken accessToken;

    @Mock
    private Bruker bruker;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private BestillingProgress bestillingProgress;

    @Captor
    ArgumentCaptor<String> statusCaptor;

    @InjectMocks
    private AaregClient aaregClient;

    @BeforeEach
    void setup() {
        statusCaptor = ArgumentCaptor.forClass(String.class);
    }

    private static ArbeidsforholdRespons buildArbeidsforhold(boolean isOrgnummer) {

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

        var request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsAareg.builder().build()));
        request.setEnvironments(singleton(ENV));
        aaregClient.gjenopprett(request,
                        DollyPerson.builder().ident(IDENT)
                                .bruker(bruker)
                                .build(), bestillingProgress, false)
                .subscribe(resultat ->
                        verify(aaregConsumer).opprettArbeidsforhold(any(Arbeidsforhold.class), eq(ENV)));
    }

    @Test
    void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnesAktoerPerson_returnsOK() {
        when(applicationConfig.getClientTimeout()).thenReturn(30L);
        var request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsAareg.builder()
                .arbeidsgiver(RsAktoerPerson.builder().ident(IDENT).build())
                .arbeidsforholdId("1")
                .build()));
        request.setEnvironments(singleton(ENV));

        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV))
                .thenReturn(Mono.just(
                        buildArbeidsforhold(false)));
        when(aaregConsumer.endreArbeidsforhold(any(Arbeidsforhold.class), eq(ENV)))
                .thenReturn(Flux.just(ArbeidsforholdRespons.builder()
                        .miljo(ENV)
                        .arbeidsforholdId("1")
                        .build()));
        when(mapperFacade.mapAsList(anyList(), eq(Arbeidsforhold.class)))
                .thenReturn(buildArbeidsforhold(false).getEksisterendeArbeidsforhold());

        StepVerifier.create(aaregClient.gjenopprett(request,
                                DollyPerson.builder().ident(IDENT)
                                        .bruker(bruker)
                                        .build(), bestillingProgress, false)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), is(equalTo("q2:Info= Oppretting startet mot AAREG ...")));
                    assertThat(statusCaptor.getAllValues().get(1), is(equalTo("q2: arbforhold=1$OK")));
                })
                .verifyComplete();
    }

    @Test
    void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnesAktoerOrganisasjon_returnsOK() {
        when(applicationConfig.getClientTimeout()).thenReturn(30L);
        var request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsAareg.builder()
                .arbeidsgiver(RsOrganisasjon.builder().orgnummer(ORGNUMMER).build())
                .arbeidsforholdId("1")
                .isOppdatering(true)
                .build()));
        request.setEnvironments(singleton(ENV));

        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV))
                .thenReturn(Mono.just(buildArbeidsforhold(true)));
        when(aaregConsumer.endreArbeidsforhold(any(Arbeidsforhold.class), eq(ENV)))
                .thenReturn(Flux.just(ArbeidsforholdRespons.builder()
                        .miljo(ENV)
                        .arbeidsforholdId("1")
                        .build()));
        when(mapperFacade.mapAsList(anyList(), eq(Arbeidsforhold.class)))
                .thenReturn(buildArbeidsforhold(true).getEksisterendeArbeidsforhold());

        StepVerifier.create(aaregClient.gjenopprett(request, DollyPerson.builder().ident(IDENT)
                                .bruker(bruker)
                                .build(), bestillingProgress, false)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), is(equalTo("q2:Info= Oppretting startet mot AAREG ...")));
                    assertThat(statusCaptor.getAllValues().get(1), is(equalTo("q2: arbforhold=1$OK")));
                })
                .verifyComplete();
    }
}