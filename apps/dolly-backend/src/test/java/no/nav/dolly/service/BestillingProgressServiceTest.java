package no.nav.dolly.service;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BestillingProgressServiceTest {

    private final static String BRUKERID = "123";
    private final static String BRUKERNAVN = "BRUKER";
    private final static String EPOST = "@@@@";

    @Mock
    private BestillingProgressRepository mockRepo;

    @InjectMocks
    private BestillingProgressService progressService;

    @Test
    public void bestillingProgressKasterExceptionHvisManIkkeFinnerProgress() throws Exception {
        when(mockRepo.findByBestillingId(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () ->
                progressService.fetchBestillingProgressByBestillingsIdFromDB(null));
    }

    @Test
    public void hviFetchBestillingProgressFinnerObjectSåReturnerObjektet() {
        BestillingProgress mock = Mockito.mock(BestillingProgress.class);
        when(mockRepo.findByBestillingId(1l)).thenReturn(Optional.of(Collections.singletonList(mock)));
        List<BestillingProgress> bes = progressService.fetchBestillingProgressByBestillingsIdFromDB(1l);
        assertThat(bes.get(0), is(mock));
    }

    @Test
    public void hvisFetchProgressSomReturnererTomListeHvisIkkeFunnetIkkeFinnerObjektSåReturnerTomListe() {
        when(mockRepo.findByBestillingId(1l)).thenReturn(Optional.of(new ArrayList<>()));
        List<BestillingProgress> bes = progressService.fetchBestillingProgressByBestillingId(1l);
        assertThat(bes.isEmpty(), is(true));
    }

    @BeforeTestClass
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(Jwt.withTokenValue("test")
                .claim("oid", BRUKERID)
                .claim("name", BRUKERNAVN)
                .claim("epost", EPOST)
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .build()));
    }

}