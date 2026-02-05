package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedfoedtBarnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO.Ansvar;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.IdentRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.PdlSkifteform;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO.OppholdType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO.Tiltakstype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TilrettelagtKommunikasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskIdentifikasjonsnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalEmbete;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalMandattype;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class DollyRequest2MalBestillingPersonMappingStrategyTest {

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new DollyRequest2MalBestillingMappingStrategy());
    }

    @Test
    void shouldAccumulatePersonAdressebeskyttelse() {

        var target = mapperFacade.map(RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .adressebeskyttelse(List.of(AdressebeskyttelseDTO.builder()
                                        .gradering(AdresseBeskyttelse.FORTROLIG)
                                        .build()))
                                .build())
                        .build())
                .build(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .adressebeskyttelse(List.of(AdressebeskyttelseDTO.builder()
                                        .gradering(AdresseBeskyttelse.STRENGT_FORTROLIG)
                                        .build()))
                                .build())
                        .build())
                .build(), target);

        assertThat(target.getPdldata().getPerson().getAdressebeskyttelse(), hasItems(
                AdressebeskyttelseDTO.builder()
                        .gradering(AdresseBeskyttelse.FORTROLIG)
                        .build(),
                AdressebeskyttelseDTO.builder()
                        .gradering(AdresseBeskyttelse.STRENGT_FORTROLIG)
                        .build()));
    }

    @Test
    void shouldAccumulatePersonBostedsadresse() {

        var target = mapperFacade.map(buildBostedsadresse("Blåbærstien", "12", "1234"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildBostedsadresse("Rødbergveien", "34", "5678"), target);

        assertThat(target.getPdldata().getPerson().getBostedsadresse(), hasItems(
                BostedadresseDTO.builder()
                        .vegadresse(VegadresseDTO.builder()
                                .adressenavn("Blåbærstien")
                                .husnummer("12")
                                .postnummer("1234")
                                .build())
                        .build(),
                BostedadresseDTO.builder()
                        .vegadresse(VegadresseDTO.builder()
                                .adressenavn("Rødbergveien")
                                .husnummer("34")
                                .postnummer("5678")
                                .build())
                        .build()));
    }

    @Test
    void shouldAccumulatePersonKontaktadresse() {

        var target = mapperFacade.map(buildKontaktadresse("Bjørenebærstien", "12", "1234"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildKontaktadresse("Rødbergveien", "34", "5678"), target);

        assertThat(target.getPdldata().getPerson().getKontaktadresse(), hasItems(
                KontaktadresseDTO.builder()
                        .vegadresse(VegadresseDTO.builder()
                                .adressenavn("Bjørenebærstien")
                                .husnummer("12")
                                .postnummer("1234")
                                .build())
                        .build(),
                KontaktadresseDTO.builder()
                        .vegadresse(VegadresseDTO.builder()
                                .adressenavn("Rødbergveien")
                                .husnummer("34")
                                .postnummer("5678")
                                .build())
                        .build()));
    }

    @Test
    void shouldAccumulatePersonOppholdsadresse() {

        var target = mapperFacade.map(buildOppholdsadresse("Blåbærstien", "12", "1234"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildOppholdsadresse("Rødbergveien", "34", "5678"), target);

        assertThat(target.getPdldata().getPerson().getOppholdsadresse(), hasItems(
                OppholdsadresseDTO.builder()
                        .vegadresse(VegadresseDTO.builder()
                                .adressenavn("Blåbærstien")
                                .husnummer("12")
                                .postnummer("1234")
                                .build())
                        .build(),
                OppholdsadresseDTO.builder()
                        .vegadresse(VegadresseDTO.builder()
                                .adressenavn("Rødbergveien")
                                .husnummer("34")
                                .postnummer("5678")
                                .build())
                        .build()));
    }

    @Test
    void shouldAccumulatePersonDeltBosted() {

        var target = mapperFacade.map(buildDeltBosted("Bjørenebærstien", "12", "1234"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildDeltBosted("Rødbergveien", "34", "5678"), target);

        assertThat(target.getPdldata().getPerson().getDeltBosted(), hasItems(
                DeltBostedDTO.builder()
                        .vegadresse(VegadresseDTO.builder()
                                .adressenavn("Bjørenebærstien")
                                .husnummer("12")
                                .postnummer("1234")
                                .build())
                        .build(),
                DeltBostedDTO.builder()
                        .vegadresse(VegadresseDTO.builder()
                                .adressenavn("Rødbergveien")
                                .husnummer("34")
                                .postnummer("5678")
                                .build())
                        .build()));
    }

    @Test
    void shouldAccumulatePersonDoedfoedtBarn() {

        var target = mapperFacade.map(buildDoedfoedtBarn(LocalDateTime.of(2025, 1, 2, 0, 0)), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildDoedfoedtBarn(LocalDateTime.of(2026, 1, 13, 0, 0)), target);

        assertThat(target.getPdldata().getPerson().getDoedfoedtBarn(), hasItems(
                DoedfoedtBarnDTO.builder()
                        .dato(LocalDateTime.of(2025, 1, 2, 0, 0))
                        .build(),
                DoedfoedtBarnDTO.builder()
                        .dato(LocalDateTime.of(2026, 1, 13, 0, 0))
                        .build()));
    }

    @Test
    void shouldAccumulatePersonDoedsfall() {

        var target = mapperFacade.map(buildDoedsfall(LocalDateTime.of(2025, 1, 2, 0, 0)), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildDoedsfall(LocalDateTime.of(2026, 1, 13, 0, 0)), target);

        assertThat(target.getPdldata().getPerson().getDoedsfall(), hasItems(
                DoedsfallDTO.builder()
                        .doedsdato(LocalDateTime.of(2025, 1, 2, 0, 0))
                        .build(),
                DoedsfallDTO.builder()
                        .doedsdato(LocalDateTime.of(2026, 1, 13, 0, 0))
                        .build()));
    }

    @Test
    void shouldAccumulatePersonFalskIdentitet() {

        var target = mapperFacade.map(buildFalskIdentitet(true, "3532235345"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildFalskIdentitet(false, "86234343"), target);

        assertThat(target.getPdldata().getPerson().getFalskIdentitet(), hasItems(
                FalskIdentitetDTO.builder()
                        .eksisterendePerson(true)
                        .rettIdentitetVedIdentifikasjonsnummer("3532235345")
                        .build(),
                FalskIdentitetDTO.builder()
                        .eksisterendePerson(false)
                        .rettIdentitetVedIdentifikasjonsnummer("86234343")
                        .build()));
    }

    @Test
    void shouldAccumulatePersonFoedested() {

        var target = mapperFacade.map(buildFoedsted("Bergen", "Minde"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildFoedsted("Trondheim", "Lade"), target);

        assertThat(target.getPdldata().getPerson().getFoedested(), hasItems(
                FoedestedDTO.builder()
                        .foedekommune("Bergen")
                        .foedested("Minde")
                        .build(),
                FoedestedDTO.builder()
                        .foedekommune("Trondheim")
                        .foedested("Lade")
                        .build()));
    }

    @Test
    void shouldAccumulatePersonFoedsel() {

        var target = mapperFacade.map(buildFoedsel("Bergen", "Minde", 1980), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildFoedsel("Trondheim", "Lade", 2005), target);

        assertThat(target.getPdldata().getPerson().getFoedsel(), hasItems(
                FoedselDTO.builder()
                        .foedekommune("Bergen")
                        .foedested("Minde")
                        .foedselsaar(1980)
                        .build(),
                FoedselDTO.builder()
                        .foedekommune("Trondheim")
                        .foedested("Lade")
                        .foedselsaar(2005)
                        .build()));
    }

    @Test
    void shouldAccumulatePersonFoedselsDato() {

        var target = mapperFacade.map(buildFoedselsdato(1980, LocalDate.of(1980, 2, 3)), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildFoedselsdato(2005, LocalDate.of(2005, 6, 7)), target);

        assertThat(target.getPdldata().getPerson().getFoedselsdato(), hasItems(
                FoedselsdatoDTO.builder()
                        .foedselsaar(1980)
                        .foedselsdato(LocalDate.of(1980, 2, 3).atStartOfDay())
                        .build(),
                FoedselsdatoDTO.builder()
                        .foedselsaar(2005)
                        .foedselsdato(LocalDate.of(2005, 6, 7).atStartOfDay())
                        .build()));
    }

    @Test
    void shouldAccumulatePersonFolkeregisterPersonstatus() {

        var target = mapperFacade.map(buildFolkeregisterPersonstatus(FolkeregisterPersonstatus.BOSATT), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildFolkeregisterPersonstatus(FolkeregisterPersonstatus.FOEDSELSREGISTRERT), target);

        assertThat(target.getPdldata().getPerson().getFolkeregisterPersonstatus(), hasItems(
                FolkeregisterPersonstatusDTO.builder()
                        .status(FolkeregisterPersonstatus.BOSATT)
                        .build(),
                FolkeregisterPersonstatusDTO.builder()
                        .status(FolkeregisterPersonstatus.FOEDSELSREGISTRERT)
                        .build()));
    }

    @Test
    void shouldAccumulatePersonForelderBarnRelasjon() {

        var target = mapperFacade.map(buildForelderBarnRelasjon(Rolle.BARN, Rolle.MOR), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildForelderBarnRelasjon(Rolle.BARN, Rolle.FAR), target);

        assertThat(target.getPdldata().getPerson().getForelderBarnRelasjon(), hasItems(
                ForelderBarnRelasjonDTO.builder()
                        .minRolleForPerson(Rolle.BARN)
                        .relatertPersonsRolle(Rolle.MOR)
                        .eksisterendePerson(false)
                        .build(),
                ForelderBarnRelasjonDTO.builder()
                        .minRolleForPerson(Rolle.BARN)
                        .relatertPersonsRolle(Rolle.FAR)
                        .eksisterendePerson(false)
                        .build()));
    }

    @Test
    void shouldAccumulatePersonForeldreansvar() {

        var target = mapperFacade.map(buildForeldreansvar(Ansvar.MOR), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildForeldreansvar(Ansvar.FAR), target);

        assertThat(target.getPdldata().getPerson().getForeldreansvar(), hasItems(
                ForeldreansvarDTO.builder()
                        .ansvar(Ansvar.MOR)
                        .eksisterendePerson(false)
                        .build(),
                ForeldreansvarDTO.builder()
                        .ansvar(Ansvar.FAR)
                        .eksisterendePerson(false)
                        .build()));
    }

    @Test
    void shouldAccumulatePersonFullemakt() {

        var target = mapperFacade.map(buildFullmakt(List.of("Fullmakt 1", "Fullmakt 2")), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildFullmakt(List.of("Fullmakt 3", "Fullmakt 4")), target);

        assertThat(target.getPdldata().getPerson().getFullmakt(), hasItems(
                FullmaktDTO.builder()
                        .omraader(List.of("Fullmakt 1", "Fullmakt 2"))
                        .eksisterendePerson(false)
                        .build(),
                FullmaktDTO.builder()
                        .omraader(List.of("Fullmakt 3", "Fullmakt 4"))
                        .eksisterendePerson(false)
                        .build()));
    }

    @Test
    void shouldAccumulatePersonInnflytting() {

        var target = mapperFacade.map(buildInnflytting("Kenya", "Nairobi"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildInnflytting("Canada", "Vancouver"), target);

        assertThat(target.getPdldata().getPerson().getInnflytting(), hasItems(
                InnflyttingDTO.builder()
                        .fraflyttingsland("Kenya")
                        .fraflyttingsstedIUtlandet("Nairobi")
                        .build(),
                InnflyttingDTO.builder()
                        .fraflyttingsland("Canada")
                        .fraflyttingsstedIUtlandet("Vancouver")
                        .build()));
    }

    @Test
    void shouldAccumulatePersonKjoenn() {

        var target = mapperFacade.map(buildKjoenn(Kjoenn.MANN), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildKjoenn(Kjoenn.KVINNE), target);

        assertThat(target.getPdldata().getPerson().getKjoenn(), hasItems(
                KjoennDTO.builder()
                        .kjoenn(Kjoenn.MANN)
                        .build(),
                KjoennDTO.builder()
                        .kjoenn(Kjoenn.KVINNE)
                        .build()));
    }

    @Test
    void shouldAccumulatePersonKontaktinformasjonForDoedsbo() {

        var target = mapperFacade.map(buildKontaktinformasjonForDoedsbo(PdlSkifteform.OFFENTLIG), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildKontaktinformasjonForDoedsbo(PdlSkifteform.ANNET), target);

        assertThat(target.getPdldata().getPerson().getKontaktinformasjonForDoedsbo(), hasItems(
                KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(PdlSkifteform.OFFENTLIG)
                        .build(),
                KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(PdlSkifteform.ANNET)
                        .build()));
    }

    @Test
    void shouldAccumulatePersonNavn() {

        var target = mapperFacade.map(buildNavn("Grønn", "Brevkasse"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildNavn("Gul", "Plastpose"), target);

        assertThat(target.getPdldata().getPerson().getNavn(), hasItems(
                NavnDTO.builder()
                        .fornavn("Grønn")
                        .etternavn("Brevkasse")
                        .build(),
                NavnDTO.builder()
                        .fornavn("Gul")
                        .etternavn("Plastpose")
                        .build()));
    }

    @Test
    void shouldAccumulatePersonNyIdent() {

        var target = mapperFacade.map(buildNyIdent(Identtype.DNR, false), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildNyIdent(Identtype.FNR, true), target);

        assertThat(target.getPdldata().getPerson().getNyident(), hasItems(
                IdentRequestDTO.builder()
                        .identtype(Identtype.DNR)
                        .id2032(false)
                        .build(),
                IdentRequestDTO.builder()
                        .identtype(Identtype.FNR)
                        .id2032(true)
                        .build()));
    }

    @Test
    void shouldAccumulatePersonOpphold() {

        var target = mapperFacade.map(buildOpphold(OppholdType.MIDLERTIDIG), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildOpphold(OppholdType.PERMANENT), target);

        assertThat(target.getPdldata().getPerson().getOpphold(), hasItems(
                OppholdDTO.builder()
                        .type(OppholdType.MIDLERTIDIG)
                        .build(),
                OppholdDTO.builder()
                        .type(OppholdType.PERMANENT)
                        .build()));
    }

    @Test
    void shouldAccumulatePersonSikkerhetstiltak() {

        var target = mapperFacade.map(buildSikkerhetstiltak(Tiltakstype.FTUS, "Flere personer tilstede"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildSikkerhetstiltak(Tiltakstype.TOAN, "Bevæpning påkrevd"), target);

        assertThat(target.getPdldata().getPerson().getSikkerhetstiltak(), hasItems(
                SikkerhetstiltakDTO.builder()
                        .tiltakstype(Tiltakstype.FTUS)
                        .beskrivelse("Flere personer tilstede")
                        .build(),
                SikkerhetstiltakDTO.builder()
                        .tiltakstype(Tiltakstype.TOAN)
                        .beskrivelse("Bevæpning påkrevd")
                        .build()));
    }

    @Test
    void shouldAccumulatePersonSivilstand() {

        var target = mapperFacade.map(buildSivilstand(Sivilstand.SAMBOER), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildSivilstand(Sivilstand.GIFT), target);

        assertThat(target.getPdldata().getPerson().getSivilstand(), hasItems(
                SivilstandDTO.builder()
                        .type(Sivilstand.SAMBOER)
                        .eksisterendePerson(false)
                        .build(),
                SivilstandDTO.builder()
                        .type(Sivilstand.GIFT)
                        .eksisterendePerson(false)
                        .build()));
    }

    @Test
    void shouldAccumulatePersonStatsborgerskap() {

        var target = mapperFacade.map(buildStatsborgerskap("Puerto Rico"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildStatsborgerskap("Senegal"), target);

        assertThat(target.getPdldata().getPerson().getStatsborgerskap(), hasItems(
                StatsborgerskapDTO.builder()
                        .landkode("Puerto Rico")
                        .build(),
                StatsborgerskapDTO.builder()
                        .landkode("Senegal")
                        .build()));
    }

    @Test
    void shouldAccumulatePersonTelefonnummer() {

        var target = mapperFacade.map(buildTelefonnummer("+31", "1231231233231"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildTelefonnummer("+49", "22131823123"), target);

        assertThat(target.getPdldata().getPerson().getTelefonnummer(), hasItems(
                TelefonnummerDTO.builder()
                        .landskode("+31")
                        .nummer("1231231233231")
                        .build(),
                TelefonnummerDTO.builder()
                        .landskode("+49")
                        .nummer("22131823123")
                        .build()));
    }

    @Test
    void shouldAccumulatePersonTilrettelagtKommunikasjon() {

        var target = mapperFacade.map(buildTilrettelagtKommunikasjon("norsk", "nynorsk"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildTilrettelagtKommunikasjon("svensk", "samisk"), target);

        assertThat(target.getPdldata().getPerson().getTilrettelagtKommunikasjon(), hasItems(
                TilrettelagtKommunikasjonDTO.builder()
                        .spraakForTaletolk("norsk")
                        .spraakForTegnspraakTolk("nynorsk")
                        .build(),
                TilrettelagtKommunikasjonDTO.builder()
                        .spraakForTaletolk("svensk")
                        .spraakForTegnspraakTolk("samisk")
                        .build()));
    }

    @Test
    void shouldAccumulatePersonUtenlandskIdentifikasjonsnummer() {

        var target = mapperFacade.map(buildUtenlandskIdentifikasjonsnummer("ABC4235345345345"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildUtenlandskIdentifikasjonsnummer("HJG42342342343"), target);

        assertThat(target.getPdldata().getPerson().getUtenlandskIdentifikasjonsnummer(), hasItems(
                UtenlandskIdentifikasjonsnummerDTO.builder()
                        .identifikasjonsnummer("ABC4235345345345")
                        .build(),
                UtenlandskIdentifikasjonsnummerDTO.builder()
                        .identifikasjonsnummer("HJG42342342343")
                        .build()));
    }

    @Test
    void shouldAccumulatePersonUtflytting() {

        var target = mapperFacade.map(buildUtflytting("Finland", "Vapriikki"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildUtflytting("Russland", "Kolomenskoye"), target);

        assertThat(target.getPdldata().getPerson().getUtflytting(), hasItems(
                UtflyttingDTO.builder()
                        .tilflyttingsland("Finland")
                        .tilflyttingsstedIUtlandet("Vapriikki")
                        .build(),
                UtflyttingDTO.builder()
                        .tilflyttingsland("Russland")
                        .tilflyttingsstedIUtlandet("Kolomenskoye")
                        .build()));
    }

    @Test
    void shouldAccumulatePersonVergemaal() {

        var target = mapperFacade.map(buildVergemaal(VergemaalEmbete.FMAV, VergemaalMandattype.FIN), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildVergemaal(VergemaalEmbete.FMRO, VergemaalMandattype.FOR), target);

        assertThat(target.getPdldata().getPerson().getVergemaal(), hasItems(
                VergemaalDTO.builder()
                        .vergemaalEmbete(VergemaalEmbete.FMAV)
                        .mandatType(VergemaalMandattype.FIN)
                        .eksisterendePerson(false)
                        .build(),
                VergemaalDTO.builder()
                        .vergemaalEmbete(VergemaalEmbete.FMRO)
                        .mandatType(VergemaalMandattype.FOR)
                        .eksisterendePerson(false)
                        .build()));
    }

    private static RsDollyUtvidetBestilling buildVergemaal(VergemaalEmbete vergemaalEmbete, VergemaalMandattype vergemaalMandattype) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .vergemaal(List.of(VergemaalDTO.builder()
                                        .vergemaalEmbete(vergemaalEmbete)
                                        .mandatType(vergemaalMandattype)
                                        .eksisterendePerson(false)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildUtflytting(String land, String sted) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .utflytting(List.of(UtflyttingDTO.builder()
                                        .tilflyttingsland(land)
                                        .tilflyttingsstedIUtlandet(sted)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildUtenlandskIdentifikasjonsnummer(String identifikasjonsnummer) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .utenlandskIdentifikasjonsnummer(List.of(UtenlandskIdentifikasjonsnummerDTO.builder()
                                        .identifikasjonsnummer(identifikasjonsnummer)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildTilrettelagtKommunikasjon(String spraak, String tegnspraak) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .tilrettelagtKommunikasjon(List.of(TilrettelagtKommunikasjonDTO.builder()
                                        .spraakForTaletolk(spraak)
                                        .spraakForTegnspraakTolk(tegnspraak)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildTelefonnummer(String landkode, String telefonnummer) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .telefonnummer(List.of(TelefonnummerDTO.builder()
                                        .landskode(landkode)
                                        .nummer(telefonnummer)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildStatsborgerskap(String landkode) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .statsborgerskap(List.of(StatsborgerskapDTO.builder()
                                        .landkode(landkode)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildSivilstand(Sivilstand sivilstand) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .sivilstand(List.of(SivilstandDTO.builder()
                                        .type(sivilstand)
                                        .eksisterendePerson(false)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildSikkerhetstiltak(Tiltakstype tiltakstype, String beskrivelse) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .sikkerhetstiltak(List.of(SikkerhetstiltakDTO.builder()
                                        .tiltakstype(tiltakstype)
                                        .beskrivelse(beskrivelse)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildOpphold(OppholdType oppholdType) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .opphold(List.of(OppholdDTO.builder()
                                        .type(oppholdType)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildNyIdent(Identtype identtype, Boolean id2032) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .nyident(List.of(IdentRequestDTO.builder()
                                        .identtype(identtype)
                                        .id2032(id2032)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildNavn(String fornavn, String etternavn) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .navn(List.of(NavnDTO.builder()
                                        .fornavn(fornavn)
                                        .etternavn(etternavn)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildKontaktinformasjonForDoedsbo(PdlSkifteform pdlSkifteform) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .kontaktinformasjonForDoedsbo(List.of(KontaktinformasjonForDoedsboDTO.builder()
                                        .skifteform(pdlSkifteform)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildKjoenn(Kjoenn kjoenn) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .kjoenn(List.of(KjoennDTO.builder()
                                        .kjoenn(kjoenn)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildInnflytting(String fraflyttingsland, String fraflyttingsstedIUtlandet) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .innflytting(List.of(InnflyttingDTO.builder()
                                        .fraflyttingsland(fraflyttingsland)
                                        .fraflyttingsstedIUtlandet(fraflyttingsstedIUtlandet)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildFullmakt(List<String> omraader) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .fullmakt(List.of(FullmaktDTO.builder()
                                        .omraader(omraader)
                                        .eksisterendePerson(false)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildForeldreansvar(Ansvar foreldreansvar) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .foreldreansvar(List.of(ForeldreansvarDTO.builder()
                                        .ansvar(foreldreansvar)
                                        .eksisterendePerson(false)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildForelderBarnRelasjon(Rolle minRolleForPerson, Rolle relatertPersonsRolle) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .forelderBarnRelasjon(List.of(ForelderBarnRelasjonDTO.builder()
                                        .minRolleForPerson(minRolleForPerson)
                                        .relatertPersonsRolle(relatertPersonsRolle)
                                        .eksisterendePerson(false)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildFolkeregisterPersonstatus(FolkeregisterPersonstatus status) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .folkeregisterPersonstatus(List.of(FolkeregisterPersonstatusDTO.builder()
                                        .status(status)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildFoedselsdato(Integer foedselsaar, LocalDate foedselsdato) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .foedselsdato(List.of(FoedselsdatoDTO.builder()
                                        .foedselsaar(foedselsaar)
                                        .foedselsdato(foedselsdato.atStartOfDay())
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildFoedsel(String foedekommune, String foedested, Integer foedselsaar) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .foedsel(List.of(FoedselDTO.builder()
                                        .foedekommune(foedekommune)
                                        .foedested(foedested)
                                        .foedselsaar(foedselsaar)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildFoedsted(String foedekommune, String foedested) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .foedested(List.of(FoedestedDTO.builder()
                                        .foedekommune(foedekommune)
                                        .foedested(foedested)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildFalskIdentitet(boolean eksisterendePerson, String rettIdentitetVedIdentifikasjonsnummer) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .falskIdentitet(List.of(FalskIdentitetDTO.builder()
                                        .eksisterendePerson(eksisterendePerson)
                                        .rettIdentitetVedIdentifikasjonsnummer(rettIdentitetVedIdentifikasjonsnummer)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildDoedsfall(LocalDateTime doedsdato) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .doedsfall(List.of(DoedsfallDTO.builder()
                                        .doedsdato(doedsdato)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildDoedfoedtBarn(LocalDateTime doedsdato) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .doedfoedtBarn(List.of(DoedfoedtBarnDTO.builder()
                                        .dato(doedsdato)
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildDeltBosted(String adressenavn, String husnummer, String postnummer) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .deltBosted(List.of(DeltBostedDTO.builder()
                                        .vegadresse(VegadresseDTO.builder()
                                                .adressenavn(adressenavn)
                                                .husnummer(husnummer)
                                                .postnummer(postnummer)
                                                .build())
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildOppholdsadresse(String adressenavn, String husnummer, String postnummer) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .oppholdsadresse(List.of(OppholdsadresseDTO.builder()
                                        .vegadresse(VegadresseDTO.builder()
                                                .adressenavn(adressenavn)
                                                .husnummer(husnummer)
                                                .postnummer(postnummer)
                                                .build())
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildKontaktadresse(String adressenavn, String husnummer, String postnummer) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .kontaktadresse(List.of(KontaktadresseDTO.builder()
                                        .vegadresse(VegadresseDTO.builder()
                                                .adressenavn(adressenavn)
                                                .husnummer(husnummer)
                                                .postnummer(postnummer)
                                                .build())
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildBostedsadresse(String adressenavn, String husnummer, String postnummer) {

        return RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder()
                                .bostedsadresse(List.of(BostedadresseDTO.builder()
                                        .vegadresse(VegadresseDTO.builder()
                                                .adressenavn(adressenavn)
                                                .husnummer(husnummer)
                                                .postnummer(postnummer)
                                                .build())
                                        .build()))
                                .build())
                        .build())
                .build();
    }
}