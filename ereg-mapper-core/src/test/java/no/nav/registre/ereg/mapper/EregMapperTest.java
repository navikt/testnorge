package no.nav.registre.ereg.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import no.nav.registre.ereg.TestUtil;
import no.nav.registre.ereg.provider.rs.request.EregDataRequest;
import no.nav.registre.ereg.provider.rs.request.Naeringskode;
import no.nav.registre.ereg.service.NameService;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {EregMapper.class})
public class EregMapperTest {

    @Mock
    private NameService nameService;

    @InjectMocks
    private EregMapper eregMapper;

    @Test
    public void mapEregRequests_RandomNaeringskode_Name() {
        when(nameService.getRandomNaeringskode()).thenReturn("01.120");
        when(nameService.getFullNames(anyList(), anyString())).thenReturn(Collections.singletonList("Gul Bolle AS"));

        EregDataRequest request = EregDataRequest.builder()
                .orgId("123")
                .type("AS")
                .endringsType("N")
                .build();
        String s = eregMapper.mapEregFromRequests(Collections.singletonList(request));
        assertEquals("HEADER " + EregMapper.getDateNowFormatted() + "00000AA A\n" +
                "ENH 123      AS NNY   " + EregMapper.getDateNowFormatted() + EregMapper.getDateNowFormatted() + "J           \n" +
                "NAVNN   Gul Bolle AS                                                                                                                                                                                                       \n" +
                "NACEN   01.120" + EregMapper.getDateNowFormatted() + "N\n" +
                "TRAIER 0000001000000005\n", s);
    }

    @Test
    public void mapEregRequests_PartialSuccess() {

        when(nameService.getFullNames(anyList(), anyString())).thenReturn(Collections.singletonList("Gul Bolle"));

        EregDataRequest request = EregDataRequest.builder()
                .orgId("123")
                .type("BEDR")
                .endringsType("N")
                .naeringskode(Naeringskode.builder()
                        .gyldighetsdato("18062019")
                        .hjelpeEnhet(false)
                        .kode("0?")
                        .build())
                .build();
        String s = eregMapper.mapEregFromRequests(Collections.singletonList(request));
        log.info(s);
        assertEquals("HEADER " + EregMapper.getDateNowFormatted() + "00000AA A", s.substring(0, 24));
        assertEquals("ENH 123      BEDNNY   " + EregMapper.getDateNowFormatted() + EregMapper.getDateNowFormatted() + "J           ", s.substring(25, 75));
        assertEquals("NAVNN   Gul Bolle                                                                                                                                                                                                          ",
                s.substring(76, 295));
    }

    @Test
    public void mapEregRequests_PartialSuccess_AS() {

        when(nameService.getFullNames(anyList(), anyString())).thenReturn(Collections.singletonList("Gul Bolle Dyrking av sukkerrør AS"));

        EregDataRequest request = EregDataRequest.builder()
                .orgId("123")
                .type("AS")
                .endringsType("N")
                .naeringskode(Naeringskode.builder()
                        .gyldighetsdato("20191806")
                        .hjelpeEnhet(false)
                        .kode("0?")
                        .build())
                .build();
        String s = eregMapper.mapEregFromRequests(Collections.singletonList(request));
        log.info(s);
        assertEquals("HEADER " + EregMapper.getDateNowFormatted() + "00000AA A", s.substring(0, 24));
        assertEquals("ENH 123      AS NNY   " + EregMapper.getDateNowFormatted() + EregMapper.getDateNowFormatted() + "J           ", s.substring(25, 75));
        assertEquals("NAVNN   Gul Bolle Dyrking av sukkerrør AS                                                                                                                                                                                  ",
                s.substring(76, 295));
    }

    @Test
    public void mapEregFromRequests_AllFieldsSuccess() {

        String s = eregMapper.mapEregFromRequests(Collections.singletonList(TestUtil.createDefaultEregData()));
        log.info(s);
        assertEquals("HEADER " + EregMapper.getDateNowFormatted() + "00000AA A", s.substring(0, 24));
        assertEquals("ENH 123      BEDNNY   " + EregMapper.getDateNowFormatted() + EregMapper.getDateNowFormatted() + "J           ", s.substring(25, 75));
        assertEquals("NAVNN   Flott                              bedrift                                                                                                                                     Tull                                ",
                s.substring(76, 295));
        assertEquals("PADRN   0175     NOR1         OSLO                              S2                                                                                                                       ",
                s.substring(296, 481));
        assertEquals("FADRN   0175     NOR1         OSLO                              S2                                                                                                                       ",
                s.substring(482, 667));
        assertEquals("EPOSN   noreply@nav.no                                                                                                                                        ",
                s.substring(668, 826));
        assertEquals("IADRN   https://www.ikkenav.no                                                                                                                                ",
                s.substring(827, 985));
        assertEquals("MÅL N   B", s.substring(986, 995));
        assertEquals("ARBGN   J", s.substring(996, 1005));
        assertEquals("ISEKN   AB      ", s.substring(1006, 1022));
        assertEquals("STIDN   18062019", s.substring(1023, 1039));
        assertEquals("TFONN   11111111", s.substring(1040, 1056));
        assertEquals("TFAXN   22222222", s.substring(1057, 1073));
        assertEquals("MTLFN   33333333", s.substring(1074, 1090));
        assertEquals("KATGN   DA   1", s.substring(1091, 1105));
        assertEquals("NDATN   18062019", s.substring(1106, 1122));
        assertEquals("BDATN   18062019", s.substring(1123, 1139));
        assertEquals("EDATN   18062019", s.substring(1140, 1156));
        assertEquals("KJRPN   J", s.substring(1157, 1166));
        assertEquals("UVNON   J", s.substring(1167, 1176));
        assertEquals("UENON   J", s.substring(1177, 1186));
        assertEquals("RVFGN   J", s.substring(1187, 1196));
        assertEquals("UREGN   098                                Utenlands navn!                                                                                          NOR OSLO                              S2                                                                                                       ",
                s.substring(1197, 1488));
        assertEquals("IPF N   ", s.substring(1489, 1497));
        assertEquals("ULOVN   J  AS      Noe som ikke er mer enn 70 tegn                                       Enda mer under 70                                                     ",
                s.substring(1498, 1657));
        assertEquals("KAPIN   2? 000000000000000111000000000000000222333                                                                   Noe som kan gå over flere records........................... Minst 70 ",
                s.substring(1658, 1845));
        assertEquals("KAPIN   2? 000000000000000111000000000000000222333                                                                   tegn for å få delt opp records i flere                                ",
                s.substring(1846, 2033));
        assertEquals("NACEN   0?    18062019N", s.substring(2034, 2057));
        assertEquals("FORMN   Jobb                                                                  ",
                s.substring(2058, 2136));
        assertEquals("FMVAN   NOE!  ", s.substring(2137, 2151));
    }
}