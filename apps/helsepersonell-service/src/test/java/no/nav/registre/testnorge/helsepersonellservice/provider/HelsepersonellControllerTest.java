//package no.nav.registre.testnorge.helsepersonellservice.provider;
//
//import no.nav.registre.testnorge.helsepersonellservice.domain.Helsepersonell;
//import no.nav.registre.testnorge.helsepersonellservice.domain.Persondata;
//import no.nav.registre.testnorge.helsepersonellservice.domain.PdlPersonBolk;
//import no.nav.registre.testnorge.helsepersonellservice.domain.Samhandler;
//import no.nav.registre.testnorge.helsepersonellservice.domain.HelsepersonellListe;
//import no.nav.registre.testnorge.helsepersonellservice.service.HelsepersonellService;
//import no.nav.testnav.libs.dto.samhandlerregisteret.v1.IdentDTO;
//import no.nav.testnav.libs.dto.samhandlerregisteret.v1.SamhandlerDTO;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.Collections;
//
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ActiveProfiles("test")
//@TestPropertySource(locations = "classpath:application-test.yml")
//@RunWith(MockitoJUnitRunner.class)
//public class HelsepersonellControllerTest {
//
//    @MockBean
//    public JwtDecoder jwtDecoder;
//
//    @Mock
//    private HelsepersonellService helsepersonellService;
//
//    @InjectMocks
//    private HelsepersonellController helsepersonellController;
//
//    private static final String ident = "12345678910";
//
//    @Test
//    public void shouldGetHelsepersonell() {
//        var samhandler = new Samhandler(SamhandlerDTO.builder()
//                .identer(Collections.singletonList(IdentDTO.builder()
//                        .ident(ident)
//                        .identTypeKode("FNR")
//                        .build()))
//                .kode("LE")
//                .build());
//
//        var metadata = new PdlPersonBolk.Metadata(false);
//        var navn = new PdlPersonBolk.Navn("Hans", "Hans", "Hansen", metadata);
//        var person = new PdlPersonBolk.Person(Collections.singletonList(navn));
//        var personBolk = new PdlPersonBolk.PersonBolk(ident, person);
//        var persondata = new Persondata(personBolk);
//
//        var helsepersonell = Collections.singletonList( new Helsepersonell(samhandler, persondata));
//
//        when(helsepersonellService.getHelsepersonell()).thenReturn(new HelsepersonellListe(helsepersonell));
//
//        var response = helsepersonellController.getHelsepersonell();
//
//        verify(helsepersonellService).getHelsepersonell();
//        assertThat(response).isNotNull();
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getHelsepersonell().get(0).getFnr()).isEqualTo(ident);
//    }
//
//}
