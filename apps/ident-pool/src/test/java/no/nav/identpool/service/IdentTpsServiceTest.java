package no.nav.identpool.service;

import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.test.mockito.MockitoExtension;
import no.nav.tps.ctg.m201.domain.PersondataFraTpsM201;
import no.nav.tps.ctg.m201.domain.PersondataFraTpsS201;
import no.nav.tps.ctg.m201.domain.TpsPersonData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.core.io.ClassPathResource;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@ExtendWith(MockitoExtension.class)
@DisplayName("Tester sjekking av identer mot TPS (MQ)")
class IdentTpsServiceTest {

    @InjectMocks
    private IdentTpsService identTpsService;

    @Test
    @DisplayName("Skal ta i bruk kø uten cache")
    void shoudThrow() throws Exception {
        TpsPersonData personData = JAXB.unmarshal(fetchTestXml(), TpsPersonData.class);
        Set<String> idents = extractIdents(personData);

        Set<TpsStatus> identerFinnes = identTpsService.checkIdentsInTps(idents, new ArrayList<>());

        assertResponseOk(personData, identerFinnes);
    }

    @Test
    @DisplayName("Sjekker at mapping av om identer finnes i TPS går ok")
    void finnesITps() throws Exception {
        TpsPersonData personData = JAXB.unmarshal(fetchTestXml(), TpsPersonData.class);
        Set<String> idents = extractIdents(personData);

        Set<TpsStatus> identerFinnes = identTpsService.checkIdentsInTps(idents, new ArrayList<>());

        assertResponseOk(personData, identerFinnes);
    }

    @Test
    @DisplayName("Sjekker at mapping av om identer finnes i TPS går ok")
    void finnesITpsNew() throws Exception {
        TpsPersonData personData = JAXB.unmarshal(fetchTestXml(), TpsPersonData.class);
        Set<String> idents = extractIdents(personData);

        Set<TpsStatus> identerFinnes = identTpsService.checkIdentsInTps(idents, new ArrayList<>());

        assertResponseOk(personData, identerFinnes);
    }

    @Test
    @DisplayName("Skal returnere når tom liste med fnr")
    void emptyFnrs() {
        Set<TpsStatus> identerFinnes = identTpsService.checkIdentsInTps(new HashSet<>(), new ArrayList<>());

        assertEquals(0, identerFinnes.size());
    }

    @Test
    @DisplayName("Skal feile ved sending av melding til kø")
    void shouldThrow() throws Exception {

        Set<String> idents = extractIdents(JAXB.unmarshal(fetchTestXml(), TpsPersonData.class));

        assertThrows(RuntimeException.class, () -> identTpsService.checkIdentsInTps(idents, new ArrayList<>()));
    }

    private void assertResponseOk(TpsPersonData personData, Set<TpsStatus> identerFinnes) {
        List<PersondataFraTpsM201.AFnr.EFnr> eFnrs = personData.getTpsSvar().getPersonDataM201().getAFnr().getEFnr();
        eFnrs.forEach(eFnr -> {
            boolean finnes = eFnr.getSvarStatus() == null || !"08".equals(eFnr.getSvarStatus().getReturStatus());
            assertTrue(identerFinnes.contains(new TpsStatus(eFnr.getFnr(), finnes)));
        });
    }

    private Set<String> extractIdents(TpsPersonData personData) {
        return personData.getTpsSvar().getPersonDataM201().getAFnr().getEFnr().stream()
                .map(PersondataFraTpsS201::getFnr)
                .collect(Collectors.toSet());
    }

    private static InputStream fetchTestXml() throws IOException {
        return (new ClassPathResource("mq/persondataSuccess.xml")).getInputStream();
    }
}
