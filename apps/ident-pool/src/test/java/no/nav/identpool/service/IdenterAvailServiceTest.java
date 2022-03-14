package no.nav.identpool.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.identpool.consumers.TpsMessagingConsumer;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.dto.TpsStatusDTO;
import no.nav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.identpool.repository.IdentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdenterAvailServiceTest {

    private static final String IDENT_1 = "11111111111";
    private static final String IDENT_2 = "22222222222";

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private IdentGeneratorService identGeneratorService;

    @Mock
    private IdentRepository identRepository;

    @Mock
    private TpsMessagingConsumer tpsMessagingConsumer;

    @InjectMocks
    private IdenterAvailService identerAvailService;

    @Captor
    private ArgumentCaptor<Set> argumentCaptor;

    private HentIdenterRequest request;

    private static TpsStatusDTO getTpsStatus(String ident, boolean inUse) {
        return TpsStatusDTO.builder()
                .ident(ident)
                .inUse(inUse)
                .build();
    }

    private static Ident getIdent(String ident) {
        return Ident.builder()
                .personidentifikator(ident)
                .build();
    }

    @BeforeEach
    public void setup() {
        request = HentIdenterRequest.builder()
                .antall(1)
                .foedtEtter(LocalDate.of(1960, 1, 1))
                .foedtFoer(LocalDate.of(2000, 12, 31))
                .identtype(Identtype.FNR)
                .kjoenn(Kjoenn.MANN)
                .rekvirertAv("TPSF")
                .syntetisk(false)
                .build();
        when(mapperFacade.map(request, HentIdenterRequest.class)).thenReturn(request);
    }

    @Test
    public void happyPathAvail() {
        when(identGeneratorService.genererIdenter(eq(request), any(Set.class)))
                .thenReturn(Set.of(IDENT_1, IDENT_2));
        when(identRepository.findByPersonidentifikatorIn(anySet()))
                .thenReturn(Set.of(getIdent(IDENT_2)));
        when(tpsMessagingConsumer.getIdenterStatuser(argumentCaptor.capture()))
                .thenReturn(Set.of(getTpsStatus(IDENT_1, false)));

        Set<TpsStatusDTO> target = identerAvailService.generateAndCheckIdenter(request, 10);
        assertThat(target, containsInAnyOrder(
                getTpsStatus(IDENT_1, false)));

        assertThat(argumentCaptor.getAllValues().size(), is(equalTo(1)));
        assertThat(argumentCaptor.getAllValues(),
                containsInAnyOrder(Set.of(IDENT_1)));
    }
}