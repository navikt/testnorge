package no.nav.brregstub.service;

import com.google.common.io.Resources;
import no.nav.brregstub.api.common.RolleKode;
import no.nav.brregstub.config.JacksonConfig;
import no.nav.brregstub.database.domene.HentRolle;
import no.nav.brregstub.database.domene.Rolleoversikt;
import no.nav.brregstub.database.repository.HentRolleRepository;
import no.nav.brregstub.database.repository.RolleoversiktRepository;
import no.nav.brregstub.generated.Grunndata;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
@ExtendWith(DollyWireMockExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(JacksonConfig.class)
@Testcontainers
class BrregServiceTest {

    private static final Integer ORGNR = 971524553;
    private static final String FNR = "010176100000";
    @Autowired
    private HentRolleRepository hentRolleRepositoryMock;
    @Autowired
    private RolleoversiktRepository rolleoversiktRepositoryMock;

    @Autowired
    private ObjectMapper objectMapper;
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
        brregService = new BrregService(rolleoversiktRepositoryMock, hentRolleRepositoryMock, objectMapper);

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
        assertThat(grunndata.getResponseHeader()
                .getProssessDato()
                .toGregorianCalendar()
                .toZonedDateTime()
                .toLocalDate()).isEqualTo((LocalDate.now()));
        assertThat(grunndata.getResponseHeader().getUnderStatus().getUnderStatusMelding())
                .hasSize(1).extracting(underStatusMelding -> assertThat(underStatusMelding.getKode()).isEqualTo(100));
        assertThat(grunndata.getMelding()).isNull();
    }

    @Test
    @DisplayName("hentRoller returnerer en gyldig responseheader med melding")
    void hentRollerForOrganisasjon() {
        var grunndata = brregService.hentRoller(ORGNR.toString());

        //assert responseheader
        assertThat(grunndata.getResponseHeader()).isNotNull();
        assertThat(grunndata.getResponseHeader().getHovedStatus()).isZero();
        assertThat(grunndata.getResponseHeader().getOrgnr()).isEqualTo(ORGNR);
        assertThat(grunndata.getResponseHeader().getTjeneste()).isEqualTo("hentRoller");
        assertThat(grunndata.getResponseHeader()
                .getProssessDato()
                .toGregorianCalendar()
                .toZonedDateTime()
                .toLocalDate()).isEqualTo((LocalDate.now()));
        assertThat(grunndata.getResponseHeader().getUnderStatus().getUnderStatusMelding())
                .hasSize(2).extracting("kode").contains(0, 1180);

        //assert melding
        assertThat(grunndata.getMelding()).isNotNull();
        assertThat(grunndata.getMelding()
                .getOrganisasjonsnummer()
                .getRegistreringsDato()
                .toGregorianCalendar()
                .toZonedDateTime()
                .toLocalDate()
                .format(
                        ISO_DATE)).isEqualTo("1999-03-12");
        assertThat(grunndata.getMelding().getOrganisasjonsnummer().getValue()).isEqualTo(ORGNR.toString());
    }

    @Test
    @DisplayName("hentRoller returnerer en gyldig kontaktperson")
    void hentRollerForOrganisasjonKontaktpersoner() {
        var grunndata = brregService.hentRoller(ORGNR.toString());

        //assert kontaktperson
        assertThat(grunndata.getMelding().getKontaktperson().getSamendring()).hasSize(1);
        var kontaktperson = grunndata.getMelding().getKontaktperson().getSamendring().get(0);
        assertThat(kontaktperson.getSamendringstype()).isEqualTo(RolleKode.KONT.name());
        assertThat(kontaktperson.getRegistreringsDato().toGregorianCalendar().toZonedDateTime().toLocalDate().format(
                ISO_DATE)).isEqualTo("2019-06-23");
        assertThat(kontaktperson.getRolle()).hasSize(1);
        assertThat(kontaktperson.getRolle().get(0).getBeskrivelse()).isEqualTo(RolleKode.DAGL.getBeskrivelse());
        assertThat(kontaktperson.getRolle().get(0).getRolletype()).isEqualTo(RolleKode.DAGL.name());
        assertPerson(kontaktperson.getRolle().get(0).getPerson().get(0));
    }

    @Test
    @DisplayName("hentRoller returnerer en gyldig deltaker")
    void hentRollerForOrganisasjonDeltaker() {
        var grunndata = brregService.hentRoller(ORGNR.toString());

        //assert deltaker
        assertThat(grunndata.getMelding().getDeltakere().getSamendring()).hasSize(1);
        var deltaker = grunndata.getMelding().getDeltakere().getSamendring().get(0);
        assertThat(deltaker.getSamendringstype()).isEqualTo(RolleKode.DELT.name());
        assertThat(deltaker.getRegistreringsDato().toGregorianCalendar().toZonedDateTime().toLocalDate().format(
                ISO_DATE)).isEqualTo("2003-01-01");
        assertThat(deltaker.getRolle()).hasSize(1);
        assertThat(deltaker.getRolle().get(0).getBeskrivelse()).isEqualTo(RolleKode.DTPR.getBeskrivelse());
        assertThat(deltaker.getRolle().get(0).getRolletype()).isEqualTo(RolleKode.DTPR.name());
        assertPerson(deltaker.getRolle().get(0).getPerson().get(0));

    }

    @Test
    @DisplayName("hentRoller returnerer en gyldig styre")
    void hentRollerForOrganisasjonStyre() {
        var grunndata = brregService.hentRoller(ORGNR.toString());

        //assert styreleder
        assertThat(grunndata.getMelding().getStyre().getSamendring()).hasSize(1);
        var styreleder = grunndata.getMelding().getStyre().getSamendring().get(0);
        assertThat(styreleder.getSamendringstype()).isEqualTo(RolleKode.STYR.name());
        assertThat(styreleder.getRegistreringsDato().toGregorianCalendar().toZonedDateTime().toLocalDate().format(
                ISO_DATE)).isEqualTo("2004-01-01");
        assertThat(styreleder.getRolle()).hasSize(2);
        assertThat(styreleder.getRolle().get(0).getBeskrivelse()).isEqualTo(RolleKode.LEDE.getBeskrivelse());
        assertThat(styreleder.getRolle().get(0).getRolletype()).isEqualTo(RolleKode.LEDE.name());
        assertThat(styreleder.getRolle().get(1).getBeskrivelse()).isEqualTo(RolleKode.MEDL.getBeskrivelse());
        assertThat(styreleder.getRolle().get(1).getRolletype()).isEqualTo(RolleKode.MEDL.name());
        assertPerson(styreleder.getRolle().get(0).getPerson().get(0));
    }

    @Test
    @DisplayName("hentRoller returnerer en gyldig komplementar")
    void hentRollerForOrganisasjonKomplementar() {
        var grunndata = brregService.hentRoller(ORGNR.toString());

        //assert komplementar
        assertThat(grunndata.getMelding().getKomplementar().getSamendring()).hasSize(1);
        var komplementar = grunndata.getMelding().getKomplementar().getSamendring().get(0);
        assertThat(komplementar.getSamendringstype()).isEqualTo(RolleKode.KOMP.name());
        assertThat(komplementar.getRegistreringsDato().toGregorianCalendar().toZonedDateTime().toLocalDate().format(
                ISO_DATE)).isEqualTo("2005-01-01");
        assertThat(komplementar.getRolle()).hasSize(1);
        assertThat(komplementar.getRolle().get(0).getBeskrivelse()).isEqualTo(RolleKode.KOMP.getBeskrivelse());
        assertThat(komplementar.getRolle().get(0).getRolletype()).isEqualTo(RolleKode.KOMP.name());
        assertPerson(komplementar.getRolle().get(0).getPerson().get(0));

    }

    @Test
    @DisplayName("hentRoller returnerer en gyldig sameiere")
    void hentRollerForOrganisasjonSameiere() {
        var grunndata = brregService.hentRoller(ORGNR.toString());

        //assert sameier
        assertThat(grunndata.getMelding().getSameiere().getSamendring()).hasSize(1);
        var sameier = grunndata.getMelding().getSameiere().getSamendring().get(0);
        assertThat(sameier.getSamendringstype()).isEqualTo(RolleKode.SAM.name());
        assertThat(sameier.getRegistreringsDato().toGregorianCalendar().toZonedDateTime().toLocalDate().format(
                ISO_DATE)).isEqualTo("2004-01-01");
        assertThat(sameier.getRolle()).hasSize(1);
        assertThat(sameier.getRolle().get(0).getBeskrivelse()).isEqualTo(RolleKode.SAM.getBeskrivelse());
        assertThat(sameier.getRolle().get(0).getRolletype()).isEqualTo(RolleKode.SAM.name());
        assertPerson(sameier.getRolle().get(0).getPerson().get(0));
    }

    @Test
    @DisplayName(
            "rolleutskrift returnerer grunndata uten melding, men med status 1 og understatus 180 hvis ikke finner data om ident i database")
    void hentRolleutskriftFinnerIkkeBruker() {
        var grunndata = brregService.hentRolleutskrift("ident");
        assertThat(grunndata.getResponseHeader()).isNotNull();
        assertThat(grunndata.getResponseHeader().getHovedStatus()).isEqualTo(1);
        assertThat(grunndata.getResponseHeader().getFodselsnr()).isEqualTo("ident");
        assertThat(grunndata.getResponseHeader().getOrgnr()).isNull();
        assertThat(grunndata.getResponseHeader().getTjeneste()).isEqualTo("hentRolleutskrift");
        assertThat(grunndata.getResponseHeader()
                .getProssessDato()
                .toGregorianCalendar()
                .toZonedDateTime()
                .toLocalDate()).isEqualTo((LocalDate.now()));
        assertThat(grunndata.getResponseHeader().getUnderStatus().getUnderStatusMelding())
                .hasSize(1).extracting(underStatusMelding -> assertThat(underStatusMelding.getKode()).isEqualTo(180));
        assertThat(grunndata.getMelding()).isNull();
    }

    @Test
    @DisplayName("rolleutskrift returnerer en gyldig responseheader med status 0")
    void hentRolleutskriftForPersonResponseHeader() {
        var grunndata = brregService.hentRolleutskrift(FNR);

        //assert responseheader
        assertThat(grunndata.getResponseHeader()).isNotNull();
        assertThat(grunndata.getResponseHeader().getHovedStatus()).isZero();
        assertThat(grunndata.getResponseHeader().getFodselsnr()).isEqualTo(FNR);
        assertThat(grunndata.getResponseHeader().getTjeneste()).isEqualTo("hentRolleutskrift");
        assertThat(grunndata.getResponseHeader()
                .getProssessDato()
                .toGregorianCalendar()
                .toZonedDateTime()
                .toLocalDate()).isEqualTo((LocalDate.now()));
        assertThat(grunndata.getResponseHeader().getUnderStatus().getUnderStatusMelding())
                .hasSize(2).extracting("kode").contains(0, 1180);
    }

    @Test
    @DisplayName("rolleutskrift returnerer en gyldig meldiong")
    void hentRolleutskriftForPersonMelding() {
        var grunndata = brregService.hentRolleutskrift(FNR);

        //assert melding
        assertThat(grunndata.getMelding()).isNotNull();
        assertThat(grunndata.getMelding()
                .getRolleInnehaver()
                .getFodselsdato()
                .getValue()
                .toGregorianCalendar()
                .toZonedDateTime()
                .toLocalDate()
                .format(ISO_DATE)).isEqualTo("1976-01-01");
        assertThat(grunndata.getMelding().getRolleInnehaver().getNavn().getNavn1()).isEqualTo("Navn");
        assertThat(grunndata.getMelding().getRolleInnehaver().getNavn().getNavn3()).isEqualTo("Navnesen");
        assertThat(grunndata.getMelding().getRolleInnehaver().getAdresse().getAdresse1()).isEqualTo("Dollyveien 1");
        assertThat(grunndata.getMelding().getRolleInnehaver().getAdresse().getPostnr()).isEqualTo("0576");
        assertThat(grunndata.getMelding().getRolleInnehaver().getAdresse().getPoststed()).isEqualTo("Oslo");
        assertThat(grunndata.getMelding().getRolleInnehaver().getAdresse().getLand().getValue()).isEqualTo("NO");
        assertThat(grunndata.getMelding().getRolleInnehaver().getAdresse().getLand().getLandkode1()).isEqualTo("NO");
    }

    @Test
    @DisplayName("rolleutskrift returnerer en gyldig grunndata med roller")
    void hentRolleutskriftForPersonRoller() {
        var grunndata = brregService.hentRolleutskrift(FNR);

        //assert roller
        assertThat(grunndata.getMelding().getRoller().getEnhet()).hasSize(2);
        assertThat(grunndata.getMelding().getRoller().getEnhet().get(0).getNr()).isEqualTo(1);
        assertThat(grunndata.getMelding().getRoller().getEnhet().get(1).getNr()).isEqualTo(2);
        var rolle1 = grunndata.getMelding().getRoller().getEnhet().get(0);
        assertThat(rolle1.getRolleBeskrivelse().getValue()).isEqualTo(RolleKode.INNH.getBeskrivelse());
        assertThat(rolle1.getOrgnr().getValue()).isEqualTo(ORGNR);
        assertThat(rolle1.getNavn().getNavn1()).isEqualTo("Jovial AS");
        assertThat(rolle1.getAdresse().getForretningsAdresse().getAdresse1()).isEqualTo("Dollyveien 1");
        assertThat(rolle1.getAdresse().getForretningsAdresse().getPostnr()).isEqualTo("0576");
        assertThat(rolle1.getAdresse().getForretningsAdresse().getPoststed()).isEqualTo("Oslo");
        assertThat(rolle1.getAdresse().getForretningsAdresse().getLand().getValue()).isEqualTo("NO");
        assertThat(rolle1.getAdresse().getPostAdresse().getAdresse1()).isEqualTo("Postadresseveien 1");
    }
}