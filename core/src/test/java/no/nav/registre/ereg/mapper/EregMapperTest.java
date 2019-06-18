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
                .nedleggelsesDato(null)
                .eierskapskifteDato(null)
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
    }
}