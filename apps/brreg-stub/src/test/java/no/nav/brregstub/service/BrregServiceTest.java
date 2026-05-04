package no.nav.brregstub.service;

import com.google.common.io.Resources;
import no.nav.brregstub.api.common.RolleKode;
import no.nav.brregstub.database.domene.HentRolle;
import no.nav.brregstub.database.domene.Rolleoversikt;
import no.nav.brregstub.database.repository.HentRolleRepository;
import no.nav.brregstub.database.repository.RolleoversiktRepository;
import no.nav.brregstub.generated.Grunndata;
import no.nav.dolly.libs.test.DollyServletSpringBootTest;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@DollyServletSpringBootTest
@ExtendWith(DollyWireMockExtension.class)
@Testcontainers
class BrregServiceTest {

    private static final Integer ORGNR = 971524553;
    private static final String FNR = "010176100000";
    @Autowired
    private HentRolleRepository hentRolleRepositoryMock;
    @Autowired
    private RolleoversiktRepository rolleoversiktRepositoryMock;

    @Autowired
    private BrregService brregService;

    private void assertPerson(Grunndata.Melding.Eierkommune.Samendring.Rolle.Person person) {
        assertThat(person.getFodselsnr()).isEqualTo("010176100000");
        assertThat(person.getFornavn()).isEqualTo("Navn");
        assertThat(person.getSlektsnavn()).isEqualTo("Navnesen");
        assertThat(person.getAdresse1()).isEqualTo("Navneveien 12");
        assertThat(person.getPostnr()).isEqualTo("0576");
        assertThat(person.getPoststed()).isEqualTo("OSLO");
        assertThat(person.getFratraadt()).isEqualTo("N");
        assertThat(person.getBeskrivelse()).isEqualTo("Lever");
        assertThat(person.getStatuskode()).isEqualTo("L");
        assertThat(person).extracting("land").extracting("value").isEqualTo("Norge");
    }

    private static String classpathToString(String path) {
        return resourceUrlToString(Resources.getResource(path));
    }

    private static String resourceUrlToString(URL url) {
        try {
            return Resources.toString(url, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Could not convert url to String" + url);
        }
    }

    @BeforeEach
    void onSetup() {
        rolleoversiktRepositoryMock.deleteAll();
        hentRolleRepositoryMock.deleteAll();

        hentRolleRepositoryMock.save(HentRolle.builder()
                .json(classpathToString("testdata/hentRoller.json"))
                .orgnr(ORGNR)
                .build());

        rolleoversiktRepositoryMock.save(Rolleoversikt.builder()
                .json(classpathToString("testdata/rolleutskrift.json"))
                .ident(FNR)
                .build());

    }

    @Test
    @DisplayName(
            "hentRoller returnerer grunndata uten melding, men med status 1 og understatus 100 hvis ikke finner data om orgnr i database")
    void hentRollerFinnerIkkeBruker() {
        var grunndata = brregService.hentRoller("123");
        assertThat(grunndata.getResponseHeader()).isNotNull();
        assertThat(grunndata.getResponseHeader().getHovedStatus()).isEqualTo(1);
        assertThat(grunndata.getResponseHeader().getOrgnr()).isEqualTo(123);
        assertThat(grunndata.getResponseHeader().getTjeneste()).isEqualTo("hentRoller");
        assertThat(grunndata.getResponseHeader().getUnderStatus().getUnderStatusMelding().get(0).getKode()).isEqualTo(100);
        assertThat(grunndata.getResponseHeader().getUnderStatus().getUnderStatusMelding().get(0).getValue())
                .isEqualTo("Enhet x aldri opprettet");
        assertThat(grunndata.getMelding()).isNull();
    }

    @Test
    @DisplayName("hentRoller returnerer en gyldig responseheader med melding")
    void hentRollerResponseHeader() {
        var grunndata = brregService.hentRoller(ORGNR.toString());
        assertThat(grunndata.getResponseHeader()).isNotNull();
        assertThat(grunndata.getResponseHeader().getHovedStatus()).isEqualTo(0);
        assertThat(grunndata.getResponseHeader().getOrgnr()).isEqualTo(ORGNR);
        assertThat(grunndata.getResponseHeader().getTjeneste()).isEqualTo("hentRoller");
        assertThat(grunndata.getResponseHeader().getUnderStatus().getUnderStatusMelding().get(0).getKode()).isEqualTo(0);
        assertThat(grunndata.getResponseHeader().getUnderStatus().getUnderStatusMelding().get(0).getValue()).isEqualTo("Data returnert");
        assertThat(grunndata.getMelding()).isNotNull();
    }

    @Test
    @DisplayName("hentRoller returnerer en gyldig kontaktperson")
    void hentRollerKontaktperson() {
        var grunndata = brregService.hentRoller(ORGNR.toString());
        var samendring = grunndata.getMelding().getKontaktperson().getSamendring().get(0);
        assertThat(samendring).isNotNull();
        assertThat(samendring.getSamendringstype()).isEqualTo(RolleKode.KONT.name());
        var roller = samendring.getRolle();

        var dagl = roller.stream().filter(rolle -> rolle.getRolletype().equals(RolleKode.DAGL.name())).findFirst().orElse(null);
        assertThat(dagl).isNotNull();
        assertThat(samendring.getRegistreringsDato().toString())
                .isEqualTo("2019-06-23");
        assertThat(dagl.getPerson()).hasSize(1);
        assertPerson(dagl.getPerson().get(0));
    }

    @Test
    @DisplayName("hentRoller returnerer en gyldig styre")
    void hentRollerStyre() {
        var grunndata = brregService.hentRoller(ORGNR.toString());
        var samendring = grunndata.getMelding().getStyre().getSamendring().get(0);
        assertThat(samendring.getSamendringstype()).isEqualTo(RolleKode.STYR.name());
        var roller = samendring.getRolle();

        var leder = roller.stream().filter(rolle -> rolle.getRolletype().equals(RolleKode.LEDE.name())).findFirst().orElse(null);
        assertThat(leder).isNotNull();
        assertThat(samendring.getRegistreringsDato().toString()).isEqualTo("2004-01-01");
        assertThat(leder.getPerson()).hasSize(1);
        assertPerson(leder.getPerson().get(0));
    }

    @Test
    @DisplayName("hentRoller returnerer en gyldig deltaker")
    void hentRollerDeltaker() {
        var grunndata = brregService.hentRoller(ORGNR.toString());
        var samendring = grunndata.getMelding().getDeltakere().getSamendring().get(0);
        assertThat(samendring.getSamendringstype()).isEqualTo(RolleKode.DELT.name());
        var roller = samendring.getRolle();

        var deltaker = roller.stream().filter(rolle -> rolle.getRolletype().equals(RolleKode.DTPR.name())).findFirst().orElse(null);
        assertThat(deltaker).isNotNull();
        assertThat(samendring.getRegistreringsDato().toString()).isEqualTo("2003-01-01");
        assertThat(deltaker.getPerson()).hasSize(1);
        assertPerson(deltaker.getPerson().get(0));
    }

    @Test
    @DisplayName("hentRoller returnerer en gyldig sameiere")
    void hentRollerSameiere() {
        var grunndata = brregService.hentRoller(ORGNR.toString());
        var samendring = grunndata.getMelding().getSameiere().getSamendring().get(0);
        assertThat(samendring.getSamendringstype()).isEqualTo(RolleKode.SAM.name());
        var roller = samendring.getRolle();

        var sameier = roller.stream().filter(rolle -> rolle.getRolletype().equals(RolleKode.SAM.name())).findFirst().orElse(null);
        assertThat(sameier).isNotNull();
        assertThat(samendring.getRegistreringsDato().toString()).isEqualTo("2004-01-01");
        assertThat(sameier.getPerson()).hasSize(1);
        assertPerson(sameier.getPerson().get(0));
    }

    @Test
    @DisplayName("hentRoller returnerer en gyldig komplementar")
    void hentRollerKomplementar() {
        var grunndata = brregService.hentRoller(ORGNR.toString());
        var samendring = grunndata.getMelding().getKomplementar().getSamendring().get(0);
        assertThat(samendring.getSamendringstype()).isEqualTo(RolleKode.KOMP.name());
        var roller = samendring.getRolle();

        var komplementar = roller.stream().filter(rolle -> rolle.getRolletype().equals(RolleKode.KOMP.name())).findFirst().orElse(null);
        assertThat(komplementar).isNotNull();
        assertThat(samendring.getRegistreringsDato().toString())
                .isEqualTo("2005-01-01");
        assertThat(komplementar.getPerson()).hasSize(1);
        assertPerson(komplementar.getPerson().get(0));
    }

    @Test
    @DisplayName(
            "rolleutskrift returnerer grunndata uten melding, men med status 1 og understatus 180 hvis ikke finner data om ident i database")
    void rolleutskriftFinnerIkkeBruker() {
        var grunndata = brregService.hentRolleutskrift("123");
        assertThat(grunndata.getResponseHeader()).isNotNull();
        assertThat(grunndata.getResponseHeader().getHovedStatus()).isEqualTo(1);
        assertThat(grunndata.getResponseHeader().getFodselsnr()).isEqualTo("123");
        assertThat(grunndata.getResponseHeader().getTjeneste()).isEqualTo("hentRolleutskrift");
        assertThat(grunndata.getResponseHeader().getUnderStatus().getUnderStatusMelding().get(0).getKode()).isEqualTo(180);
        assertThat(grunndata.getResponseHeader().getUnderStatus().getUnderStatusMelding().get(0).getValue())
                .isEqualTo("Personen x finnes ikke i vår database");
        assertThat(grunndata.getMelding()).isNull();
    }

    @Test
    @DisplayName("rolleutskrift returnerer en gyldig responseheader med status 0")
    void rolleutskriftResponseHeader() {
        var grunndata = brregService.hentRolleutskrift(FNR);
        assertThat(grunndata.getResponseHeader()).isNotNull();
        assertThat(grunndata.getResponseHeader().getHovedStatus()).isEqualTo(0);
        assertThat(grunndata.getResponseHeader().getFodselsnr()).isEqualTo(FNR);
        assertThat(grunndata.getResponseHeader().getTjeneste()).isEqualTo("hentRolleutskrift");
        assertThat(grunndata.getResponseHeader().getUnderStatus().getUnderStatusMelding().get(0).getKode()).isEqualTo(0);
        assertThat(grunndata.getResponseHeader().getUnderStatus().getUnderStatusMelding().get(0).getValue()).isEqualTo("Data returnert");
        assertThat(grunndata.getMelding()).isNotNull();
    }

    @Test
    @DisplayName("rolleutskrift returnerer en gyldig melding")
    void rolleutskriftMelding() {
        var grunndata = brregService.hentRolleutskrift(FNR);
        assertThat(grunndata.getMelding()).isNotNull();
        assertThat(grunndata.getMelding().getRoller().getEnhet()).hasSize(2);
    }

    @Test
    @DisplayName("rolleutskrift returnerer en gyldig grunndata med roller")
    void rolleutskriftRoller() {
        var grunndata = brregService.hentRolleutskrift(FNR);
        var enheter = grunndata.getMelding().getRoller().getEnhet();

        var innehaver = enheter.stream().filter(enhet -> enhet.getRolleBeskrivelse().getValue().equals(RolleKode.INNH.getBeskrivelse())).findFirst().orElse(null);
        assertThat(innehaver).isNotNull();
        assertThat(innehaver.getRegistreringsDato().toString()).isEqualTo("2020-01-01");
        assertThat(innehaver.getOrgnr().getValue()).isEqualTo(971524553);
        assertThat(innehaver.getNavn().getNavn1()).isEqualTo("Jovial AS");
        assertThat(innehaver.getAdresse().getForretningsAdresse().getAdresse1()).isEqualTo("Dollyveien 1");
        assertThat(innehaver.getAdresse().getForretningsAdresse().getLand().getValue()).isEqualTo("NO");
        assertThat(innehaver.getAdresse().getForretningsAdresse().getPostnr()).isEqualTo("0576");
        assertThat(innehaver.getAdresse().getForretningsAdresse().getPoststed()).isEqualTo("Oslo");
    }
}
