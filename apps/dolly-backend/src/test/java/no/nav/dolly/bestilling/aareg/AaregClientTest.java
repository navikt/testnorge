package no.nav.dolly.bestilling.aareg;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.util.CurrentAuthentication;
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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @InjectMocks
    private AaregClient aaregClient;

    @BeforeEach
    void setup() {
        when(aaregConsumer.getAccessToken()).thenReturn(Mono.just(accessToken));
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
        try (MockedStatic<CurrentAuthentication> currentAuthentication = mockStatic(CurrentAuthentication.class)) {
            currentAuthentication.when(() -> CurrentAuthentication.getAuthUser(any()))
                    .thenReturn(Bruker.builder().brukertype(Bruker.Brukertype.BANKID).build());

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
                            .opprettetIPDL(true).build(), new BestillingProgress(), false);

            verify(aaregConsumer).opprettArbeidsforhold(any(Arbeidsforhold.class), eq(ENV), eq(accessToken));

        }
    }

    @Test
    void gjenopprettArbeidsforhold_intetTidligereArbeidsforholdFinnes_lesKasterException() {
        try (MockedStatic<CurrentAuthentication> currentAuthentication = mockStatic(CurrentAuthentication.class)) {
            currentAuthentication.when(() -> CurrentAuthentication.getAuthUser(any()))
                    .thenReturn(Bruker.builder().brukertype(Bruker.Brukertype.BANKID).build());

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
                            .opprettetIPDL(true).build(), new BestillingProgress(), false);
            verify(aaregConsumer).opprettArbeidsforhold(any(Arbeidsforhold.class), eq(ENV), eq(accessToken));

        }
    }

    @Test
    void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnesAktoerPerson_returnsOK() {
        try (MockedStatic<CurrentAuthentication> currentAuthentication = mockStatic(CurrentAuthentication.class)) {
            currentAuthentication.when(() -> CurrentAuthentication.getAuthUser(any()))
                    .thenReturn(Bruker.builder().brukertype(Bruker.Brukertype.BANKID).build());

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

            aaregClient.gjenopprett(request,
                    DollyPerson.builder().hovedperson(IDENT)
                            .opprettetIPDL(true).build(), progress, false);

            assertThat(progress.getAaregStatus(), is(equalTo("u2: arbforhold=1$OK")));

        }
    }

    @Test
    void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnesAktoerOrganisasjon_returnsOK() {
        try (MockedStatic<CurrentAuthentication> currentAuthentication = mockStatic(CurrentAuthentication.class)) {
            currentAuthentication.when(() -> CurrentAuthentication.getAuthUser(any()))
                    .thenReturn(Bruker.builder().brukertype(Bruker.Brukertype.BANKID).build());

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

            aaregClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT)
                    .opprettetIPDL(true).build(), progress, false);

            assertThat(progress.getAaregStatus(), is(equalTo("u2: arbforhold=1$OK")));
        }
    }

}