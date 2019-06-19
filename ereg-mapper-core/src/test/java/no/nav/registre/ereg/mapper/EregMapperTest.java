package no.nav.registre.ereg.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import no.nav.registre.ereg.provider.rs.request.Adresse;
import no.nav.registre.ereg.provider.rs.request.EregDataRequest;
import no.nav.registre.ereg.provider.rs.request.Kapital;
import no.nav.registre.ereg.provider.rs.request.Maalform;
import no.nav.registre.ereg.provider.rs.request.Naeringskode;
import no.nav.registre.ereg.provider.rs.request.Navn;
import no.nav.registre.ereg.provider.rs.request.Telefon;
import no.nav.registre.ereg.provider.rs.request.UnderlagtHjemland;
import no.nav.registre.ereg.provider.rs.request.UtenlandsRegister;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EregMapper.class)
public class EregMapperTest {

    private EregDataRequest data;

    @Autowired
    private EregMapper eregMapper;

    @Before
    public void setUp() {

        ArrayList<String> navneListe = new ArrayList<>();
        navneListe.add("Flott");
        navneListe.add("bedrift");

        ArrayList<String> adresser = new ArrayList<>();
        adresser.add("S2");

        Map<String, String> statuser = new HashMap<>();
        statuser.put("IPF", "N");

        data = EregDataRequest.builder()
                .orgId("123")
                .navn(Navn.builder()
                        .navneListe(navneListe)
                        .redNavn("Tull")
                        .build())
                .type("BEDR")
                .endringsType("N")
                .adresse(Adresse.builder()
                        .adresser(adresser)
                        .kommuneNr("1")
                        .landKode("NOR")
                        .postNr("0175")
                        .postSted("OSLO")
                        .build())
                .forretningsAdresse(Adresse.builder()
                        .adresser(adresser)
                        .kommuneNr("1")
                        .landKode("NOR")
                        .postNr("0175")
                        .postSted("OSLO")
                        .build())
                .epost("noreply@nav.no")
                .internetAdresse("https://www.ikkenav.no")
                .frivilligRegistreringerMVA(Collections.singletonList("NOE!"))
                .harAnsatte(true)
                .sektorKode("AB")
                .stiftelsesDato("18062019")
                .telefon(Telefon.builder()
                        .fast("11111111")
                        .fax("22222222")
                        .mobil("33333333")
                        .build())
                .frivillighetsKode("DA")
                .nedleggelsesDato("18062019")
                .eierskapskifteDato("18062019")
                .oppstartsDato("18062019")
                .maalform(Maalform.B)
                .utelukkendeVirksomhetINorge(true)
                .heleidINorge(true)
                .fravalgAvRevisjonen(false)
                .utenlandsRegister(UtenlandsRegister.builder()
                        .adresse(Adresse.builder()
                                .adresser(adresser)
                                .kommuneNr("1")
                                .landKode("NOR")
                                .postNr("0175")
                                .postSted("OSLO")
                                .build())
                        .navn(Collections.singletonList("Utenlands navn!"))
                        .registerNr("098")
                        .build())
                .statuser(statuser)
                .kjoensfordeling(true)
                .underlagtHjemland(UnderlagtHjemland.builder()
                        .beskrivelseHjemland("Noe som ikke er mer enn 70 tegn")
                        .beskrivelseNorge("Enda mer under 70")
                        .foretaksformHjemland("AS")
                        .underlagtLovgivningLandkoode("J")
                        .build())
                .kapital(Kapital.builder()
                        .valuttakode("2?")
                        .kapital("101")
                        .kapitalBundet("503")
                        .kapitalInnbetalt("2222")
                        .fritekst("Noe som kan gå over flere records........................... Minst 70 tegn for å få delt opp records i flere")
                        .build())
                .naeringskode(Naeringskode.builder()
                        .gyldighetsdato("18062019")
                        .hjelpeEnhet(false)
                        .kode("0?")
                        .build())
                .formaal("Jobb")
                .build();
    }

    @Test
    public void mapEregFromRequests() {

        String s = eregMapper.mapEregFromRequests(Collections.singletonList(data));
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
        assertEquals("KAPIN   2? 000000000000000101000000000000002222503                                                                   Noe som kan gå over flere records........................... Minst 70 ",
                s.substring(1658, 1845));
        assertEquals("KAPIN   2? 000000000000000101000000000000002222503                                                                   tegn for å få delt opp records i flere                                ",
                s.substring(1846, 2033));
        assertEquals("NACEN   0?    18062019N", s.substring(2034, 2057));
        assertEquals("FORMN   Jobb                                                                  ",
                s.substring(2058, 2136));
        assertEquals("FMVAN   NOE!  ", s.substring(2137, 2151));







    }
}